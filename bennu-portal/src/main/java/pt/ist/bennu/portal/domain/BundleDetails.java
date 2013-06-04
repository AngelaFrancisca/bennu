package pt.ist.bennu.portal.domain;

import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.dsi.commons.i18n.LocalizedString;

public class BundleDetails extends Details {

    private static final long serialVersionUID = 1L;

    private final String bundle;

    private final String title;

    private final String description;

    public BundleDetails(String bundle, String title, String description) {
        this.bundle = bundle;
        this.title = title;
        this.description = description;
    }

    @Override
    public LocalizedString getDescription() {
        return BundleUtil.getInternationalString(bundle, description);
    }

    @Override
    public LocalizedString getTitle() {
        return BundleUtil.getInternationalString(bundle, title);
    }

}
