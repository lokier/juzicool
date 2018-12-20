
package com.juzicool.gather.processor;

import java.util.ArrayList;
import java.util.List;

import com.juzicool.data.Juzi;
import com.juzicool.gather.BasePageProcessor;
import com.juzicool.gather.Gloabal;
import com.juzicool.gather.utils.SelectableUtls;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class JuzimiProcessor extends BasePageProcessor {

	private boolean parseCase1(Html html,List<Juzi> result) {
		List lists = html.xpath("div[@class='views-field views-field-phpcode']").nodes();

		if(lists.size() > 0) {
			for(int i = 0 ;i < lists.size();i++) {
				Selectable selct = (Selectable)lists.get(i);

				Selectable juziE =selct.xpath("a[@class='xlistju']/text()");
				Selectable fromE=selct.xpath("span[@class='views-field-field-oriarticle-value']/text()");
				Selectable authorE =selct.xpath("a[@class='views-field-field-oriwriter-value']/text()");

				Juzi juzi = new Juzi();
				juzi.content = SelectableUtls.toSimpleText(juziE);
				juzi.from = fromE.toString();
				juzi.author = authorE.toString();

				write(juzi);


			}
			return true;
		}
		return false;
	}

	private boolean parseCase2(Html html,List<Juzi> result) {
		List lists = html.xpath("div[@class='views-field-phpcode']").nodes();

		if(lists.size() > 0) {
			for(int i = 0 ;i < lists.size();i++) {
				Selectable selct = (Selectable)lists.get(i);

				Selectable juziE =selct.xpath("a[@class='xlistju']");

				Selectable fromE=selct.xpath("span[@class='views-field-field-oriarticle-value']/text()");
				Selectable authorE =selct.xpath("a[@class='views-field-field-oriwriter-value']/text()");

				Juzi juzi = new Juzi();
				juzi.content = SelectableUtls.toSimpleText(juziE);
				juzi.from = fromE.toString();
				juzi.author = authorE.toString();

				write(juzi);


			}
			if(result.size()>0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void process(Page page) {
		String html = page.getHtml().toString();

		String simpleText = SelectableUtls.toSimpleText(page.getHtml());
		System.err.println("is succuess : " + page.isDownloadSuccess());
		System.out.println(simpleText);
		System.out.println("is flag : " + simpleText.contains("没有收录任何句子"));

		ArrayList<Juzi> rest = new ArrayList<>();
		if(parseCase1(page.getHtml(), rest)
				|| parseCase2(page.getHtml(),rest)
				) {
		}
	}

	public static void main(String[] args) {

		Gloabal.beforeMain();


		JuzimiProcessor p = new JuzimiProcessor();

		//String urlPrfix = "https://www.juzimi.com/tags/%E5%8F%8B%E6%83%85";
		Spider spider =  Spider.create(p);



		//spider.addUrl("https://www.juzimi.com/album/2364?page=0");
		spider.addUrl("https://www.juzimi.com/album/481295");  //优美的句子,美好,难过，或暂，长久,难忘

		spider.thread(1).run();
	}


	private static class JuziAlbums{
		public String urlPreffix;

	}

}
