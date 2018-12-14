
package com.juzicool.gather.processor.juzimi;

import com.juzicool.gather.Gloabal;
import com.juzicool.gather.Juzi;
import com.juzicool.gather.utils.SelectableUtls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;


/***
 * 句子迷的专辑页面。
 */
public class JuzimiAlbumProcessor extends JuzimiProcessor {

	private HashMap<String,JuziAlbum> mMap = new HashMap<>();

	@Override
	protected String getFileName(){
		return "句子迷句集"+EXCEL_XLS;
	}
	
	@Override
	protected int minJuziLength() {
		return 10;
	}

	@Override
	protected Juzi crateJuzi(String url){
		Juzi juzi = new Juzi();

		int index = url.indexOf("?");
		if(index!=-1){
			url = url.substring(0,index);
		}

		JuziAlbum album = mMap.get(url);
		if(album!= null){
			juzi.category = album.categoy;
			juzi.applyTags =album.applyTags;
			juzi.tags = album.tags;
			juzi.remark = album.remark;
		}

		return juzi;
	}

	public void addAlbum(JuziAlbum album){
		mMap.put(album.url,album);
	}


	public static void main(String[] args) {

		Gloabal.beforeMain();

		JuzimiAlbumProcessor p = new JuzimiAlbumProcessor();

		Spider spider =  Spider.create(p);

		/*
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/107424").setPageSize(1).setCategoy("哲理").setRemark("睿智是最高目标，思想主导一切。").setTags("正能量，经典").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/2364").setPageSize(2).setCategoy("感悟").setRemark("美好,难过，或暂，长久,难忘").setTags("唯美").setApplyTags("写作"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/1131886").setPageSize(2).setCategoy("台词").setRemark("结婚誓词，自肺腑的寥寥数语是两人对婚姻和一辈子的坚定承诺，最动人的誓言").setTags("感动").setApplyTags("结婚，表白，感动人"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/3903593").setPageSize(4).setCategoy("名言").setRemark("描写励志的名人名言，那些励志的名人句子和经典语录").setTags("正能量").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/56503").setPageSize(4).setCategoy("感悟").setRemark("儿女长情，思念、爱慕之前，令人感动和暧昧").setTags("古风，唯美、感动").setApplyTags("写情，唯美"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/357108").setPageSize(11).setCategoy("台词").setRemark("触动我们的影视作品").setTags("正能量、经典、感动").setApplyTags("对话台词"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/713900").setPageSize(12).setCategoy("情感").setRemark("生活中总有一些事，会让你刹那间的感动，刹那间的心疼，忽然间就泪流满面").setTags("伤感，唯美").setApplyTags("写情、伤感"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/3903667").setPageSize(3).setCategoy("友情").setRemark("描写友情的名人名言，那些关于友情、友谊、朋友的名人句子和经典语录，有关友情好友的名言警句").setTags("经典").setApplyTags("写友情"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/37711").setPageSize(15).setCategoy("思念").setRemark("怀念那些不能被重拾的美好时光").setTags("伤感，唯美").setApplyTags("写怀念"));

		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/1131943").setPageSize(2).setCategoy("台词").setRemark("实用的结婚祝福语").setTags("感动，经典").setApplyTags("结婚，表白，感动人"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/48576").setPageSize(6).setCategoy("感悟").setRemark("美的感人，说到心坎里面了").setTags("唯美、感动").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/342498").setPageSize(19).setCategoy("感悟").setRemark("唯美语录").setTags("伤感，唯美").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/107424").setPageSize(29).setCategoy("情感").setRemark("世上有一种爱情，要历经千辛万苦，或许与世界为敌，才能换得“在一起”这三个字。世人理应多宽容").setTags("伤感，唯美").setApplyTags("写耽美小说"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/144942").setPageSize(7).setCategoy("情感").setRemark("滴墨成伤，伤感，怀念").setTags("伤感，唯美").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/23325").setPageSize(7).setCategoy("诗词").setRemark("唯美的诗词").setTags("伤感，唯美").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/39826").setPageSize(10).setCategoy("哲理").setRemark("睿智是最高目标，思想主导一切。").setTags("正能量，经典").setApplyTags(""));



		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/709656").setPageSize(3).setCategoy("心语").setRemark("那些年看哭了的经典句子").setTags("伤感，唯美").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/440").setPageSize(2).setCategoy("歌词").setRemark("几句歌词，记录某年某月某日某一刻的一种心情，一种压抑，一种悲伤，一种快乐。").setTags("经典").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/109977").setPageSize(3).setCategoy("台词").setRemark("孟京辉、廖一梅、赖声川等的戏剧经典台词精选").setTags("经典").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/550969").setPageSize(7).setCategoy("心语").setRemark("最喜欢的句话").setTags("经典").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/428").setPageSize(2).setCategoy("心语").setRemark("经典唯美伤感句子").setTags("唯美").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/151260").setPageSize(27).setCategoy("歌词").setRemark("有些歌词总不经意的让人红了眼眶，或许因为感动；或许因为被说中了心事；也或许因为歌词刚好描述着自己的故事。").setTags("伤感").setApplyTags("写歌、感动"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/19199").setPageSize(2).setCategoy("语录").setRemark("很有意思的各个行业版本。起源于高晓松挺舒淇的微博，太欢乐啦。").setTags("伤感，唯美").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/330591").setPageSize(11).setCategoy("心语").setRemark("那些在我心房停留过的美丽文字，一字、一词，一句，打动心扉。").setTags("伤感，唯美").setApplyTags(""));

		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/23319").setPageSize(15).setCategoy("心语").setRemark("总会有些歌词，深入骨髓，在独自一人的时候撞击我。").setTags("伤感，经典").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/334083").setPageSize(6).setCategoy("台词").setRemark("中外文学史上数十部文学名著的开场白").setTags("经典").setApplyTags("开场白、开头"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/255428").setPageSize(9).setCategoy("心语").setRemark("此去经年，应是良辰好景虚设。 便纵有千种风情，更与何人说").setTags("伤感，唯美").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/439").setPageSize(2).setCategoy("歌词").setRemark("在我猝不及防的时候，这句歌词击中我").setTags("经典").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/632471").setPageSize(2).setCategoy("思念").setRemark("关于名字、姓氏的暖句、由来").setTags("伤感").setApplyTags("名字，姓氏"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/1752916").setPageSize(6).setCategoy("情感").setRemark("撩妹技巧").setTags("经典").setApplyTags("撩妹"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/1364017").setPageSize(23).setCategoy("心语").setRemark("陶人喜欢、治愈的句子").setTags("治愈、唯美").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/571").setPageSize(2).setCategoy("台词").setRemark("好电影有很多，好台词也有很多。那些闪过银幕的句子，感动过我的瞬间，记录下来。").setTags("经典").setApplyTags("电影台词，对话"));

		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/252457").setPageSize(7).setCategoy("心语").setRemark("往事如烟、以前的事、旧时的心情").setTags("伤感，唯美").setApplyTags("写以前"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/429258").setPageSize(56).setCategoy("情感").setRemark("耽美小说，耽美文不多，才刚刚腐").setTags("伤感，唯美").setApplyTags("耽美小说"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/431").setPageSize(4).setCategoy("爱情").setRemark("爱情心语").setTags("经典").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/433231").setPageSize(16).setCategoy("台词").setRemark("电影经典台词").setTags("伤感").setApplyTags("电影台词对话"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/429").setPageSize(3).setCategoy("台词").setRemark("QQ或论坛适用的经典个性签名").setTags("新颖").setApplyTags("个性签名，个人备注"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/554459").setPageSize(13).setCategoy("心语").setRemark("优美、古风句子").setTags("古风").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/293273").setPageSize(5).setCategoy("情感").setRemark("毕业、分离的伤感").setTags("伤感").setApplyTags("毕业、离别、分离"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/3903547").setPageSize(4).setCategoy("名言").setRemark("那些关于读书、阅读的名人句子和经典语录").setTags("哲理").setApplyTags("读书"));


		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/434").setPageSize(3).setCategoy("心语").setRemark("每个人都有心中最柔软的部分，这些句子，在某个瞬间，曾经击中我心中最柔软的那部分").setTags("伤感").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/257068").setPageSize(10).setCategoy("情感").setRemark("那些路过心上的句子").setTags("伤感").setApplyTags("思绪"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/585237").setPageSize(9).setCategoy("心语").setRemark("为高考作文优选佳句，高考必备").setTags("经典").setApplyTags("高考作文"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/89556").setPageSize(50).setCategoy("感悟").setRemark("最安静的心跳与思绪才是我们真实的自己，为自己保留几年，生产期随时，但味道也许会变，最真实的自己献给青春， 城市自述").setTags("治愈").setApplyTags("城市里忙碌"));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/66884").setPageSize(7).setCategoy("心语").setRemark("韶华已逝，青春未晚，逝去的青春").setTags("伤感").setApplyTags(""));



		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("情感").setRemark("每一句，都是一段故事。").setTags("唯美、情感").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(4).setCategoy("").setRemark("").setTags("").setApplyTags(""));

*/
		p.addAlbum(JuziAlbum.build().setUrl("https://www.juzimi.com/album/50584").setPageSize(1).setCategoy("").setRemark("").setTags("").setApplyTags(""));

		//
		for(JuziAlbum album : p.mMap.values()) {
			for(String url:album.toUrls()) {
				spider.addUrl(url);
			}
		}
		
		//spider.addUrl("https://www.juzimi.com/album/2364?page=1");  //优美的句子,美好,难过，或暂，长久,难忘

		spider.thread(1).run();
	}



	private static class JuziAlbum{
		public String url;
		public int pageSize;

		public String categoy;
		public String remark; //点评
		public String tags; //鉴赏标签
		public String applyTags; //应用标签
		
		public static JuziAlbum build() {
			return new JuziAlbum();
		}

		public JuziAlbum setUrl(String url) {
			this.url = url;
			return this;
		}

		public JuziAlbum setPageSize(int pageSize) {
			this.pageSize = pageSize;
			return this;
		}



		public JuziAlbum setCategoy(String categoy) {
			this.categoy = categoy;
			return this;
		}

	

		public JuziAlbum setRemark(String remark) {
			this.remark = remark;
			return this;
		}

	

		public JuziAlbum setTags(String tags) {
			this.tags = tags;
			return this;
		}

	

		public JuziAlbum setApplyTags(String applyTags) {
			this.applyTags = applyTags;
			return this;
		}
		
		public List<String> toUrls(){
			ArrayList<String> list = new ArrayList<>();
			
			for(int i = 0; i < this.pageSize;i++) {
				list.add(this.url+"?page=" + i);
			}
			
			return list;
		}


	}

}
