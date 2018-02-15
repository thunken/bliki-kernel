package info.bliki.api.query;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests Parse query.
 */
public class ParseTest extends BaseQueryTest {

    @Test public void test001() {
        RequestBuilder request = Parse.create().text("{{Project:Sandbox}}").title("A Sandbox Template test");
        assertThat(request.toString()).isEqualTo("action=parse&amp;format=xml&amp;text={{Project:Sandbox}}&amp;title=A Sandbox Template test");
    }

    /**
     * To make this test work correctly uncomment it and be sure that you have a
     * internet connection running.
     */
    // @Test public void testParseQuery() {
    //
    // User user = getAnonymousUser();
    // Connector connector = new Connector();
    // user = connector.login(user);
    // // System.out.println(user.getToken());
    // RequestBuilder request = Parse.create().page("Main Page");
    // ParseData parseData = connector.parse(user, request);
    // assertNotNull(parseData);
    // assertNotNull(parseData.getText());
    // String html = StringEscapeUtils.unescapeHtml(parseData.getText());
    // // System.out.println("Retrieved html text:\n" + html);
    // }

    /**
     * To make this test work correctly uncomment it and the
     * <code>assertNotNull(user);</code> line and define a correct user in the
     * <code>getRegisteredUser()</code> method
     *
     * @see BaseQueryTest#getRegisteredUser()
     */
    // public void __testParseRegisteredQuery() {
    // User user = getRegisteredUser();
    // Connector connector = new Connector();
    // user = connector.login(user);
    // // assertNotNull(user);
    // if (user != null) {
    // assertTrue(user.getToken().length() > 0);
    // System.out.println(user.getToken());
    // RequestBuilder request = Parse.create().page("Main Page");
    // ParseData parseData = connector.parse(user, request);
    // assertNotNull(parseData);
    // assertNotNull(parseData.getText());
    // String html = StringEscapeUtils.unescapeHtml(parseData.getText());
    // System.out.println("Retrieved html text:\n" + html);
    // }
    // }

}
