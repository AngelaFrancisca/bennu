package org.fenixedu.bennu.io.domain;

import org.fenixedu.bennu.core.domain.Bennu;

public class FileSupport extends FileSupport_Base {
    private FileSupport() {
        super();
    }

    public static FileSupport getInstance() {
        if (Bennu.getInstance().getFileSupport() == null) {
            Bennu.getInstance().setFileSupport(new FileSupport());
        }
        return Bennu.getInstance().getFileSupport();
    }
}
