/*
 * @(#)ContentContextInjectionRewriter.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.presentationTier.servlets.filters.contentRewrite;

import javax.servlet.http.HttpServletRequest;

import myorg.presentationTier.Context;
import myorg.presentationTier.actions.ContextBaseAction;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriterFilter.RequestRewriter;

public class ContentContextInjectionRewriter extends RequestRewriter {

    public static final String HAS_CONTEXT_PREFIX_STRING = "HAS_CONTEXT";

    public static final String BLOCK_HAS_CONTEXT_STRING = "BLOCK_HAS_CONTEXT";

    public static final String BLOCK_END_HAS_CONTEXT_STRING = "END_BLOCK_HAS_CONTEXT";

    public static final String HAS_CONTEXT_PREFIX = "<!-- " + HAS_CONTEXT_PREFIX_STRING + " -->";

    public static final char[] BLOCK_HAS_CONTEXT_PREFIX = ("<!-- " + BLOCK_HAS_CONTEXT_STRING + " -->").toCharArray();

    public static final char[] END_BLOCK_HAS_CONTEXT_PREFIX = ("<!-- " + BLOCK_END_HAS_CONTEXT_STRING + " -->").toCharArray();

    public static final String CONTEXT_ATTRIBUTE_NAME = ContextBaseAction.CONTEXT_PATH;

    private static final char[] LINK_IDENTIFIER = "<a".toCharArray();

    private static final char[] FORM_IDENTIFIER = "<form".toCharArray();

    private static final char[] IMG_IDENTIFIER = "<img".toCharArray();

    private static final char[] AREA_IDENTIFIER = "<area".toCharArray();

    private static final int LENGTH_OF_HAS_CONTENT_PREFIX = HAS_CONTEXT_PREFIX.length();

    private final String contextPath;

    public ContentContextInjectionRewriter(final HttpServletRequest httpServletRequest) {
	super(httpServletRequest);
	final Context context = ContextBaseAction.getContext(httpServletRequest);
	contextPath = context == null ? null : context.getPath();
    }

    private static final char[] CLOSE = ">".toCharArray();
    private static final char[] PREFIX_JAVASCRIPT = "javascript:".toCharArray();
    private static final char[] PREFIX_MAILTO = "mailto:".toCharArray();
    private static final char[] PREFIX_HTTP = "http:".toCharArray();
    private static final char[] PREFIX_HTTPS = "https:".toCharArray();
    private static final char[] CARDINAL = "#".toCharArray();
    private static final char[] QUESTION_MARK = "?".toCharArray();

    @Override
    public StringBuilder rewrite(final StringBuilder source) {
	if (contextPath == null || contextPath.length() == 0) {
	    return source;
	}

	final StringBuilder response = new StringBuilder();

	int iOffset = 0;

	while (true) {
	    final int indexOfAopen = indexOf(source, LINK_IDENTIFIER, iOffset);
	    final int indexOfFormOpen = indexOf(source, FORM_IDENTIFIER, iOffset);
	    final int indexOfImgOpen = indexOf(source, IMG_IDENTIFIER, iOffset);
	    final int indexOfAreaOpen = indexOf(source, AREA_IDENTIFIER, iOffset);
	    final int indexOfBlockHasContextopen = indexOf(source, BLOCK_HAS_CONTEXT_PREFIX, iOffset);

	    if (firstIsMinValue(indexOfAopen, indexOfFormOpen, indexOfImgOpen, indexOfAreaOpen, indexOfBlockHasContextopen)) {
		if (!isPrefixed(source, indexOfAopen)) {
		    final int indexOfAclose = indexOf(source, CLOSE, indexOfAopen);
		    if (indexOfAclose >= 0) {
			final int indexOfHrefBodyStart = findHrefBodyStart(source, indexOfAopen, indexOfAclose);
			if (indexOfHrefBodyStart >= 0) {
			    final char hrefBodyStartChar = source.charAt(indexOfHrefBodyStart - 1);
			    final int indexOfHrefBodyEnd = findHrefBodyEnd(source, indexOfHrefBodyStart, hrefBodyStartChar);
			    if (indexOfHrefBodyEnd >= 0) {
				int indexOfJavaScript = indexOf(source, PREFIX_JAVASCRIPT, indexOfHrefBodyStart);
				int indexOfMailto = indexOf(source, PREFIX_MAILTO, indexOfHrefBodyStart);
				int indexOfHttp = indexOf(source, PREFIX_HTTP, indexOfHrefBodyStart);
				int indexOfHttps = indexOf(source, PREFIX_HTTPS, indexOfHrefBodyStart);
				if ((indexOfJavaScript < 0 || indexOfJavaScript > indexOfHrefBodyEnd)
					&& (indexOfMailto < 0 || indexOfMailto > indexOfHrefBodyEnd)
					&& (indexOfHttp < 0 || indexOfHttp > indexOfHrefBodyEnd)
					&& (indexOfHttps < 0 || indexOfHttps > indexOfHrefBodyEnd)) {
				    final int indexOfCardinal = indexOf(source, CARDINAL, indexOfHrefBodyStart);
				    boolean hasCardinal = indexOfCardinal > indexOfHrefBodyStart
					    && indexOfCardinal < indexOfHrefBodyEnd;
				    if (hasCardinal) {
					response.append(source, iOffset, indexOfCardinal);
				    } else {
					response.append(source, iOffset, indexOfHrefBodyEnd);
				    }

				    final int indexOfQmark = indexOf(source, QUESTION_MARK, indexOfHrefBodyStart);
				    if (indexOfQmark == -1 || indexOfQmark > indexOfHrefBodyEnd) {
					response.append('?');
				    } else {
					response.append("&amp;");
				    }
				    appendContextParameter(response);

				    if (hasCardinal) {
					response.append(source, indexOfCardinal, indexOfHrefBodyEnd);
				    }

				    iOffset = continueToNextToken(response, source, indexOfHrefBodyEnd, indexOfAclose);
				    continue;
				}
			    }
			}
		    }
		}
		iOffset = continueToNextToken(response, source, iOffset, indexOfAopen);
		continue;
	    } else if (firstIsMinValue(indexOfFormOpen, indexOfAopen, indexOfImgOpen, indexOfAreaOpen, indexOfBlockHasContextopen)) {
		if (!isPrefixed(source, indexOfFormOpen)) {
		    final int indexOfFormClose = indexOf(source, CLOSE, indexOfFormOpen);
		    if (indexOfFormClose >= 0) {
			final int indexOfFormActionBodyStart = findFormActionBodyStart(source, indexOfFormOpen, indexOfFormClose);
			if (indexOfFormActionBodyStart >= 0) {
			    final int indexOfFormActionBodyEnd = findFormActionBodyEnd(source, indexOfFormActionBodyStart);
			    if (indexOfFormActionBodyEnd >= 0) {
				iOffset = continueToNextToken(response, source, iOffset, indexOfFormClose);
				appendContextAttribute(response);
				continue;
			    }
			}
		    }
		}
		iOffset = continueToNextToken(response, source, iOffset, indexOfFormOpen);
		continue;
	    } else if (firstIsMinValue(indexOfImgOpen, indexOfAopen, indexOfFormOpen, indexOfAreaOpen, indexOfBlockHasContextopen)) {
		if (!isPrefixed(source, indexOfImgOpen)) {
		    final int indexOfImgClose = indexOf(source, CLOSE, indexOfImgOpen);
		    if (indexOfImgClose >= 0) {
			final int indexOfSrcBodyStart = findSrcBodyStart(source, indexOfImgOpen, indexOfImgClose);
			if (indexOfSrcBodyStart >= 0) {
			    final int indexOfSrcBodyEnd = findSrcBodyEnd(source, indexOfSrcBodyStart);
			    if (indexOfSrcBodyEnd >= 0) {
				response.append(source, iOffset, indexOfSrcBodyEnd);

				final int indexOfQmark = indexOf(source, QUESTION_MARK, indexOfSrcBodyStart);
				if (indexOfQmark == -1 || indexOfQmark > indexOfSrcBodyEnd) {
				    response.append('?');
				} else {
				    response.append("&amp;");
				}
				appendContextParameter(response);

				iOffset = continueToNextToken(response, source, indexOfSrcBodyEnd, indexOfImgClose);
				continue;
			    }
			}
		    }
		}
		iOffset = continueToNextToken(response, source, iOffset, indexOfImgOpen);
		continue;
	    } else if (firstIsMinValue(indexOfAreaOpen, indexOfAopen, indexOfFormOpen, indexOfImgOpen, indexOfBlockHasContextopen)) {
		if (!isPrefixed(source, indexOfAreaOpen)) {
		    final int indexOfAreaClose = indexOf(source, CLOSE, indexOfAreaOpen);
		    if (indexOfAreaClose >= 0) {
			final int indexOfHrefBodyStart = findHrefBodyStart(source, indexOfAreaOpen, indexOfAreaClose);
			if (indexOfHrefBodyStart >= 0) {
			    final char hrefBodyStartChar = source.charAt(indexOfHrefBodyStart - 1);
			    final int indexOfHrefBodyEnd = findHrefBodyEnd(source, indexOfHrefBodyStart, hrefBodyStartChar);
			    if (indexOfHrefBodyEnd >= 0) {
				final int indexOfCardinal = indexOf(source, CARDINAL, indexOfHrefBodyStart);
				boolean hasCardinal = indexOfCardinal > indexOfHrefBodyStart
					&& indexOfCardinal < indexOfHrefBodyEnd;
				if (hasCardinal) {
				    response.append(source, iOffset, indexOfCardinal);
				} else {
				    response.append(source, iOffset, indexOfHrefBodyEnd);
				}

				final int indexOfQmark = indexOf(source, QUESTION_MARK, indexOfHrefBodyStart);
				if (indexOfQmark == -1 || indexOfQmark > indexOfHrefBodyEnd) {
				    response.append('?');
				} else {
				    response.append("&amp;");
				}
				appendContextParameter(response);

				if (hasCardinal) {
				    response.append(source, indexOfCardinal, indexOfHrefBodyEnd);
				}

				iOffset = continueToNextToken(response, source, indexOfHrefBodyEnd, indexOfAreaClose);
				continue;
			    }
			}
		    }
		}
		iOffset = continueToNextToken(response, source, iOffset, indexOfAreaOpen);
		continue;
	    } else if (firstIsMinValue(indexOfBlockHasContextopen, indexOfAopen, indexOfFormOpen, indexOfImgOpen, indexOfAreaOpen)) {
		final int indexOfEndBlockHasContextOpen = indexOf(source, END_BLOCK_HAS_CONTEXT_PREFIX, indexOfBlockHasContextopen);
		if (indexOfEndBlockHasContextOpen == -1) {
		    iOffset = indexOfBlockHasContextopen + BLOCK_HAS_CONTEXT_PREFIX.length;
		} else {
		    response.append(source, iOffset, indexOfEndBlockHasContextOpen);
		    iOffset = indexOfEndBlockHasContextOpen;
		}
		continue;
	    } else {
		response.append(source, iOffset, source.length());
		break;
	    }
	}

	return response;
    }

    private void appendContextParameter(final StringBuilder response) {
	response.append(CONTEXT_ATTRIBUTE_NAME);
	response.append("=");
	response.append(contextPath);
    }

    private void appendContextAttribute(final StringBuilder response) {
	response.append("<input type=\"hidden\" name=\"");
	response.append(CONTEXT_ATTRIBUTE_NAME);
	response.append("\" value=\"");
	response.append(contextPath);
	response.append("\"/>");
    }

    protected int continueToNextToken(final StringBuilder response, final StringBuilder source, final int iOffset,
	    final int indexOfTag) {
	final int nextOffset = indexOfTag + 1;
	response.append(source, iOffset, nextOffset);
	return nextOffset;
    }

    protected boolean isPrefixed(final StringBuilder source, final int indexOfTagOpen) {
	return indexOfTagOpen >= LENGTH_OF_HAS_CONTENT_PREFIX
		&& match(source, indexOfTagOpen - LENGTH_OF_HAS_CONTENT_PREFIX, indexOfTagOpen, HAS_CONTEXT_PREFIX);
    }

    protected boolean match(final StringBuilder source, final int iStart, int iEnd, final String string) {
	final int length = string.length();
	if (iEnd - iStart != length) {
	    return false;
	}
	for (int i = 0; i < length; i++) {
	    if (source.charAt(iStart + i) != string.charAt(i)) {
		return false;
	    }
	}
	return true;
    }

    protected boolean firstIsMinValue(final int index, final int... indexes) {
	if (index >= 0) {
	    for (final int otherIndex : indexes) {
		if (otherIndex >= 0 && otherIndex < index) {
		    return false;
		}
	    }
	    return true;
	}
	return false;
    }

}