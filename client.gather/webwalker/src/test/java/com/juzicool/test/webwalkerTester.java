package com.juzicool.test;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Test;

public class webwalkerTester {

    @Test
    public void testSimple(){
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
           // Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());
            System.out.println( page.getTitleText());

            final String pageAsXml = page.asXml();
            //Assert.assertTrue(pageAsXml.contains("<body class=\"composite\">"));
            System.out.println( pageAsXml);

            final String pageAsText = page.asText();

            System.out.println( pageAsText);

            //Assert.assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
