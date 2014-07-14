package org.fenixedu.bennu.core.domain;

import java.util.Locale;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;

import com.google.common.base.Strings;

public class UserProfile extends UserProfile_Base {
    protected UserProfile() {
        super();
    }

    public UserProfile(String givenNames, String familyNames, String displayName, String email, Locale preferredLocale) {
        this();
        changeName(givenNames, familyNames, displayName);
        setEmail(email);
        setPreferredLocale(preferredLocale);
    }

    @Override
    public String getDisplayName() {
        return super.getDisplayName() != null ? super.getDisplayName() : getFullName();
    }

    @Override
    public String getGivenNames() {
        // FIXME: remove when framework support read-only slots
        return super.getGivenNames();
    }

    @Override
    public String getFamilyNames() {
        // FIXME: remove when framework support read-only slots
        return super.getFamilyNames();
    }

    public String getFullName() {
        StringBuilder builder = new StringBuilder();
        if (getGivenNames() != null) {
            builder.append(getGivenNames());
        }
        builder.append(" ");
        if (getFamilyNames() != null) {
            builder.append(getFamilyNames());
        }
        return builder.toString().trim();
    }

    public void changeName(String given, String family, String display) {
        setGivenNames(cleanupName(given));
        setFamilyNames(cleanupName(family));
        setDisplayName(cleanupName(display));
        validateNames(getDisplayName(), getFullName());
    }

    private static void validateNames(String displayname, String fullname) {
        if (displayname == null) {
            return;
        }
        if (fullname == null) {
            throw BennuCoreDomainException.displayNameNotContainedInFullName(displayname, fullname);
        }
        String[] fullnameparts = fullname.split(" ");
        String[] displayparts = displayname.split(" ");
        for (int n = 0, f = 0; n < displayparts.length; n++, f++) {
            if (fullnameparts.length == f) {
                throw BennuCoreDomainException.displayNameNotContainedInFullName(displayname, fullname);
            }
            while (!displayparts[n].equalsIgnoreCase(fullnameparts[f])) {
                f++;
                if (fullnameparts.length == f) {
                    throw BennuCoreDomainException.displayNameNotContainedInFullName(displayname, fullname);
                }
            }
        }
    }

    private static String cleanupName(String name) {
        if (name == null) {
            return null;
        }
        return Strings.emptyToNull(name.trim().replaceAll("\\s+", " "));
    }
}
