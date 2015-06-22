/*
 * User.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.domain;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

/**
 * The application end user.
 */
public final class User extends User_Base implements Principal {
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    private static SecureRandom prng = null;

    private static Map<String, User> map = new ConcurrentHashMap<>();

    static {
        try {
            prng = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Please provide SHA1PRNG implementation", e);
        }
    }

    public static final Comparator<User> COMPARATOR_BY_NAME = Comparator.comparing(User::getDisplayName).thenComparing(
            User::getUsername);

    public static interface UsernameGenerator {
        public String doGenerate(UserProfile parameter);
    }

    public User(UserProfile profile) {
        this(generateUsername(profile), profile);
    }

    public User(String username, UserProfile profile) {
        super();
        if (findByUsername(username) != null) {
            throw BennuCoreDomainException.duplicateUsername(username);
        }
        setBennu(Bennu.getInstance());
        setCreated(new DateTime());
        setUsername(username);
        setProfile(profile);
    }

    @Override
    public String getUsername() {
        //FIXME: remove when the framework enables read-only slots
        return super.getUsername();
    }

    @Override
    public DateTime getCreated() {
        //FIXME: remove when the framework enables read-only slots
        return super.getCreated();
    }

    public LocalDate getExpiration() {
        LocalDate latest = null;
        for (UserLoginPeriod period : getLoginValiditySet()) {
            // If there is an open period, set the user's expiration to null (i.e. open)
            if (period.getEndDate() == null) {
                return null;
            }

            // If no expiration is defined, or the current expiration is before the period's end date,
            // set it as the expiration.
            if (latest == null || latest.isBefore(period.getEndDate())) {
                latest = period.getEndDate();
            }
        }
        return latest;
    }

    @Override
    public String getName() {
        return getUsername();
    }

    public String getDisplayName() {
        return getProfile().getDisplayName();
    }

    public String getEmail() {
        return getProfile().getEmail();
    }

    public String generatePassword() {
        final String password = UUID.randomUUID().toString().replace("-", "").substring(0, 15);
        changePassword(password);
        return password;
    }

    public void changePassword(final String password) {
        if (getPassword() != null) {
            String newHashWithOldSalt = Hashing.sha512().hashString(getSalt() + password, Charsets.UTF_8).toString();
            if (newHashWithOldSalt.equals(getPassword())) {
                throw BennuCoreDomainException.badOldPassword();
            }
        }
        byte salt[] = new byte[64];
        prng.nextBytes(salt);
        setSalt(BaseEncoding.base64().encode(salt));
        String hash = Hashing.sha512().hashString(getSalt() + password, Charsets.UTF_8).toString();
        setPassword(hash);
    }

    public boolean isLoginExpired() {
        return getExpiration() != null && new LocalDate().isAfter(getExpiration());
    }

    public boolean matchesPassword(final String password) {
        if (getPassword() == null) {
            return true;
        }
        final String hash = Hashing.sha512().hashString(getSalt() + password, Charsets.UTF_8).toString();
        return hash.equals(getPassword());
    }

    public Group groupOf() {
        return Group.users(this);
    }

    public static User findByUsername(final String username) {
        if (username == null) {
            return null;
        }
        User match = map.computeIfAbsent(username, User::manualFind);
        if (match == null) {
            return null;
        }
        // FIXME: the second condition is there because of bug #197 in the fenix-framework
        if (!FenixFramework.isDomainObjectValid(match) || !match.getUsername().equals(username)) {
            map.remove(username, match);
            return findByUsername(username);
        }
        return match;
    }

    private static User manualFind(String username) {
        return Bennu.getInstance().getUserSet().stream().filter(user -> user.getUsername().equals(username)).findAny()
                .orElse(null);
    }

    public static void setUsernameGenerator(UsernameGenerator generator) {
        usernameGenerator = generator;
    }

    private static UsernameGenerator usernameGenerator = new UsernameGenerator() {
        private final AtomicInteger currentId = new AtomicInteger(0);

        @Override
        public String doGenerate(UserProfile profile) {
            return "bennu" + currentId.getAndIncrement();
        }
    };

    private static String generateUsername(UserProfile profile) {
        while (true) {
            String username = usernameGenerator.doGenerate(profile);
            if (User.findByUsername(username) == null) {
                logger.debug("Generated username {} for {}", username, profile);
                return username;
            }
        }
    }
}
