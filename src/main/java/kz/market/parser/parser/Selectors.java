package kz.market.parser.parser;

public class Selectors {
    public static final String LISTING = "ul > li > article > div > a";

    public static class Main {
        public static final String NAME                 = "div[aria-label=\"Property header\"]";
        public static final String PRICE                = "span[aria-label=\"Price\"]";
        public static final String INTERVAL             = "span[aria-label=\"Frequency\"]";
        public static final String IMAGES               = "a > picture > img[src]";
        public static final String BEDS                 = "span[aria-label=\"Beds\"] > span";
        public static final String BATHS                = "span[aria-label=\"Baths\"] > span";
        public static final String SQFT                 = "span[aria-label=\"Area\"] > span";
        public static final String DESCRIPTION          = "div[aria-label=\"Property description\"]  > div > span";
    }

    public static class Agency {
        public static final String AGENT_URL            = "span > a[aria-label=\"Agent name\"]";
        public static final String AGENCY_NAME          = "span[aria-label=\"Agency name\"]";
        public static final String AGENCY_IMAGE         = "img[aria-label=\"Agency logo\"]";
        public static final String REGISTRATION_NUMBER  = "span[aria-label=\"Agency name\"] ~ div > span";
        public static final String AGENT_NAME           = "span > a[aria-label=\"Agent name\"]";
        public static final String AGENT_PHOTO          = "div[aria-label=\"Agency info\"] > div > div div > div > picture > img";
    }

    public static class PropertyInfo {
        public static final String TYPE                 = "[aria-label=\"Type\"]";
        public static final String PURPOSE              = "[aria-label=\"Purpose\"]";
        public static final String REFERENCE            = "[aria-label=\"Reference\"]";
        public static final String FURNISHING           = "[aria-label=\"Furnishing\"]";
        public static final String DATE_POSTED          = "[aria-label=\"Reactivated date\"]";
        public static final String COMPLETION           = "[aria-label=\"Completion status\"]";
        public static final String HAS_AVERAGE_RENT     = "[aria-label=\"Average Rent\"]";
        public static final String AVERAGE_RENT         = "[aria-label=\"Average Rent\"] > div > div > span";
        public static final String AVERAGE_RENT_FREQ    = "[aria-label=\"Average Rent\"] [aria-label=\"Frequency\"]";
        public static final String TRUE_CHECK           = "[aria-label=\"Trucheck date\"]";
        public static final String GUIDED_INFO          = "[aria-label=\"Guide link title\"]";
    }

}
