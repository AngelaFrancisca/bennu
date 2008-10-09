package myorg.persistenceTier;

import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

public class MultiLanguageString2SqlMultiLanguageStringConversion implements FieldConversion {

    public Object javaToSql(Object source) throws ConversionException {
	if (source instanceof MultiLanguageString) {
	    return ((MultiLanguageString) source).exportAsString();
	}

	return source;
    }

    public Object sqlToJava(Object source) throws ConversionException {
	if (source == null) {
	    return null;
	}
	if (source.equals("")) {
	    return new MultiLanguageString();
	}
	if (source instanceof String) {
	    return MultiLanguageString.importFromString((String) source);
	}

	return null;
    }

}
