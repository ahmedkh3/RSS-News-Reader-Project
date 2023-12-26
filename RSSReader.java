import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.xmltree.XMLTree;
import components.xmltree.XMLTree1;

/**
 * Program to convert an XML RSS (version 2.0) feed from a given URL into the
 * corresponding HTML output file.
 *
 * @author Ahmed Hassen
 *
 */
public final class RSSReader {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private RSSReader() {
    }

    /**
     * Outputs the "opening" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * <html> <head> <title>the channel tag title as the page title</title>
     * </head> <body>
     * <h1>the page title inside a link to the <channel> link</h1>
     * <p>
     * the channel description
     * </p>
     * <table border="1">
     * <tr>
     * <th>Date</th>
     * <th>Source</th>
     * <th>News</th>
     * </tr>
     *
     * @param channel
     *            the channel element XMLTree
     * @param out
     *            the output stream
     * @updates out.content
     * @requires [the root of channel is a <channel> tag] and out.is_open
     * @ensures out.content = #out.content * [the HTML "opening" tags]
     */
    private static void outputHeader(XMLTree channel, SimpleWriter out) {
        assert channel != null : "Violation of: channel is not null";
        assert out != null : "Violation of: out is not null";
        assert channel.isTag() && channel.label().equals("channel") : ""
                + "Violation of: the label root of channel is a <channel> tag";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("<html>");
        out.println("<head>");
        out.println("<title>");
        int indexofTitle = getChildElement(channel, "title");
        String titleLabel = "";
        //if title has children, print it out else "No Title".
        if (channel.child(indexofTitle).numberOfChildren() > 0) {
            out.print(channel.child(indexofTitle).label());
            titleLabel = channel.child(indexofTitle).child(0).label();
        } else {
            out.print("No Title");
        }

        out.println("</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>");
        //finds the label for link, then prints the title label with link label
        //embedded inside of it.
        int indexOfLink = getChildElement(channel, "link");
        String linkLabel = channel.child(indexOfLink).child(0).label();

        out.print("<a href= " + "\"" + linkLabel + "\"" + "> " + titleLabel
                + "</a>");

        out.println("</h1>");
        out.println("<p>");
        //checks for description children, if so print it.
        int indexOfDescription = getChildElement(channel, "description");
        if (channel.child(indexOfDescription).numberOfChildren() > 0) {
            out.print(channel.child(indexOfDescription).child(0).label());
        } else {
            out.print("no description");
        }
        out.println("</p>");
        out.println("<table border =\"1 \"> ");
        out.println("<tr>");
        out.println("<th>");
        out.println("date");

        out.println("</th>");
        out.println("<th>");
        out.println("source");
        out.println("</th>");
        out.println("<th>");
        out.println("News");
        out.println("</th>");
        out.println("</tr>");
    }

    /**
     * Outputs the "closing" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * </table>
     * </body> </html>
     *
     * @param out
     *            the output stream
     * @updates out.contents
     * @requires out.is_open
     * @ensures out.content = #out.content * [the HTML "closing" tags]
     */
    private static void outputFooter(SimpleWriter out) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Finds the first occurrence of the given tag among the children of the
     * given {@code XMLTree} and return its index; returns -1 if not found.
     *
     * @param xml
     *            the {@code XMLTree} to search
     * @param tag
     *            the tag to look for
     * @return the index of the first child of type tag of the {@code XMLTree}
     *         or -1 if not found
     * @requires [the label of the root of xml is a tag]
     * @ensures <pre>
     * getChildElement =
     *  [the index of the first child of type tag of the {@code XMLTree} or
     *   -1 if not found]
     * </pre>
     */
    private static int getChildElement(XMLTree xml, String tag) {
        assert xml != null : "Violation of: xml is not null";
        assert tag != null : "Violation of: tag is not null";
        assert xml.isTag() : "Violation of: the label root of xml is a tag";
        int i = 0;
        int index = -1;
        boolean indexFound = false;
        //continue to search for index of element and ends when found or past
        //length. Returns -1 if no index found.

        while (i < xml.numberOfChildren() && indexFound == false) {
            if (xml.child(i).label().equals(tag)) {
                index = i;
                indexFound = true;
            }
            i++;
        }
        return index;
    }

    /**
     * Processes one news item and outputs one table row. The row contains three
     * elements: the publication date, the source, and the title (or
     * description) of the item.
     *
     * @param item
     *            the news item
     * @param out
     *            the output stream
     * @updates out.content
     * @requires [the label of the root of item is an <item> tag] and
     *           out.is_open
     * @ensures <pre>
     * out.content = #out.content *
     *   [an HTML table row with publication date, source, and title of news item]
     * </pre>
     */
    private static void processItem(XMLTree item, SimpleWriter out) {
        assert item != null : "Violation of: item is not null";
        assert out != null : "Violation of: out is not null";
        assert item.isTag() && item.label().equals("item") : ""
                + "Violation of: the label root of item is an <item> tag";
        assert out.isOpen() : "Violation of: out.is_open";
        String sourceLink = "";
        String sourceLabel = "";
        String newsTitle = "";
        String newsLink = "";
        String description = "";
        int indexOfPubDate = getChildElement(item, "pubDate");
        int indexOfSource = getChildElement(item, "source");
        int indexOfTitle = getChildElement(item, "title");
        int indexOfLink = getChildElement(item, "link");
        int indexOfDescription = getChildElement(item, "description");
        out.println("<tr>");
        //date section, check for date if exist.
        if (indexOfPubDate != -1) {
            out.println("<td>");
            out.println(item.child(indexOfPubDate).child(0).label());
            out.println("</td>");
        } else {
            out.println("<td>");
            out.println("pubdate doesn't exist");
            out.println("</td>");

        }
        //source section, check for source if exist.
        if (indexOfSource != -1) {
            boolean hasUrl = item.child(indexOfSource).hasAttribute("url");

            //checks for url attribute, if so make a clickable link for source
            if (hasUrl) {
                out.println("<td>");
                sourceLink = item.child(indexOfSource).attributeValue("url");
                if (item.child(indexOfSource).numberOfChildren() > 0) {
                    sourceLabel = item.child(indexOfSource).child(0).label();
                    out.print("<a href= " + "\"" + sourceLink + "\"" + "> "
                            + sourceLabel + "</a>");
                    //otherwise, print nolabel message with url link embedded.
                } else {
                    String noLabel = "No label exist";
                    out.print("<a href= " + "\"" + sourceLink + "\"" + "> "
                            + noLabel + "</a>");
                }
                out.println("</td>");
            }
        } else {
            //otherwise print no source.
            out.println("<td>");
            out.println("No source available");
            out.println("</td>");

        }
        //news section if else that checks for title or description
        if (indexOfTitle != -1) {
            //check if title has any children, and if link exists.
            if (item.child(indexOfTitle).numberOfChildren() > 0
                    && indexOfLink != -1) {
                newsTitle = item.child(indexOfTitle).child(0).label();
                newsLink = item.child(indexOfLink).child(0).label();
                out.println("<td>");
                out.println("<a href= " + "\"" + newsLink + "\"" + "> "
                        + newsTitle + "</a>");
                out.println("</td>");
            }
            //otherwise, check if description exists, and link exists.
        } else {
            if (item.child(indexOfDescription).numberOfChildren() > 0
                    && indexOfLink != -1) {
                description = item.child(indexOfDescription).child(0).label();

                newsLink = item.child(indexOfLink).child(0).label();
                out.println("<td>");
                out.println("<a href= " + "\"" + newsLink + "\"" + "> "
                        + description + "</a>");
                out.println("</td>");
                //else print no title or description.
            } else {
                String noN = "no title or description";
                out.println("<td>");
                out.println("<a href= " + "\"" + newsLink + "\"" + "> " + noN
                        + "</a>");
                out.println("</td>");
            }

        }

        out.println("</tr>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        out.print("Enter an RSS 2.0 URL: ");
        String rss = in.nextLine();
        XMLTree xml = new XMLTree1(rss);
        out.print("Enter output file name: ");
        String file = in.nextLine();
        //if statement needed for rss validity check. check label is rss,
        //check attribute value for version, 2.0
        if (xml.label().equals("rss")
                && xml.attributeValue("version").equals("2.0")) {

            XMLTree channel = xml.child(0);
            SimpleWriter htmlPage = new SimpleWriter1L(file + ".html");
            outputHeader(channel, htmlPage);

            //loops to see if url has any items, then calls processItem.
            int i = 0;
            while (i < channel.numberOfChildren()) {
                if (channel.child(i).label().equals("item")) {
                    processItem(channel.child(i), htmlPage);
                }
                i++;
            }
            outputFooter(htmlPage);
        } else {
            out.print("invalid url");
        }

        in.close();
        out.close();
    }

}
