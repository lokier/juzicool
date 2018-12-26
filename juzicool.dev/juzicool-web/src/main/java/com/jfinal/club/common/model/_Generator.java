/**
 * 请勿将俱乐部专享资源复制给其他人，保护知识产权即是保护我们所在的行业，进而保护我们自己的利益
 * 即便是公司的同事，也请尊重 JFinal 作者的努力与付出，不要复制给同事
 * 
 * 如果你尚未加入俱乐部，请立即删除该项目，或者现在加入俱乐部：http://jfinal.com/club
 * 
 * 俱乐部将提供 jfinal-club 项目文档与设计资源、专用 QQ 群，以及作者在俱乐部定期的分享与答疑，
 * 价值远比仅仅拥有 jfinal club 项目源代码要大得多
 * 
 * JFinal 俱乐部是五年以来首次寻求外部资源的尝试，以便于有资源创建更加
 * 高品质的产品与服务，为大家带来更大的价值，所以请大家多多支持，不要将
 * 首次的尝试扼杀在了摇篮之中
 */

package com.jfinal.club.common.model;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.club.common.JFinalClubConfig;
import com.jfinal.template.source.ClassPathSource;
import com.sun.org.apache.xpath.internal.operations.Mod;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * Model、BaseModel、_MappingKit 生成器
 */
public class _Generator {

	/**
	 * 部分功能使用 Db + Record 模式实现，无需生成 model 的 table 在此配置
	 */
	private static String[] excludedTable = {
			"news_feed_reply",  // 暂不实现该功能
			"project_page_view", "share_page_view", "feedback_page_view",
			"login_log",
			"sensitive_words",
			"upload_counter",
			"task_run_log",
			"message_tip",
			"friend",
            "project_like", "share_like", "feedback_like",
            "share_reply_like", "feedback_reply_like",
            "like_message_log",
            "account_role", "role_permission"
	};

	/**
	 * 重用 JFinalClubConfig 中的数据源配置，避免冗余配置
	 */
	public static DataSource getDataSource() {
		DruidPlugin druidPlugin = JFinalClubConfig.getDruidPlugin();
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}

	private static String detectSrcPath() {
		try {
			String path = PathKit.class.getResource("/").toURI().getPath();
			return new File(path).getParentFile().getParentFile().getParentFile().getCanonicalPath();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class ModuleClassLoader extends ClassLoader{
		private File mDir;



		public ModuleClassLoader(String dir){
			mDir = new File(dir);
		}




		@Override
		protected URL findResource(String name) {

			int index = name.lastIndexOf("/");
			String fileName = name.substring(index+1);

			File file = new File(mDir,fileName);
			if(file.exists()){
				try {
					return file.toURL();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}

			return super.findResource(name);
		}
	}

/*	public static void main(String[] args) {


		try{
			Class<?> aClass = moduleClassLoader.loadClass(_Generator.class.getCanonicalName());

			Method saddMethod2 = aClass.getMethod("_main", new Class[] {String[].class });

			Object g =  aClass.newInstance();
			//Constructor c=aClass.getConstructor(DataSource.class,String.class,String.class,String.class,String.class);//获取有参构造
			//gen = (Generator)c.newInstance(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
			saddMethod2.invoke(g,new Object[]{args});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/


	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "com.jfinal.club.common.model.base";

		String moduelDir = detectSrcPath();

		ClassLoader moduleClassLoader = new ModuleClassLoader(moduelDir+"/db-generate-jf");
		ClassPathSource.gBackUPClassLoadr = moduleClassLoader;


		// base model 文件保存路径
		String baseModelOutputDir = moduelDir + "/src/main/java/com/jfinal/club/common/model/base";


		System.out.println("工程目录："+ moduelDir);
		System.out.println("输出路径："+ baseModelOutputDir);



		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.jfinal.club.common.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";



		Generator gen = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);

		System.out.println("Generator classloard："+ gen.getClass().getClassLoader());

		// 设置数据库方言
		gen.setDialect(new MysqlDialect());
		// 添加不需要生成的表名
		for (String table : excludedTable) {
			gen.addExcludedTable(table.trim());
		}
		// 设置是否在 Model 中生成 dao 对象
		gen.setGenerateDaoInModel(false);
		// 设置是否生成字典文件
		gen.setGenerateDataDictionary(false);

		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		// gernerator.setRemovedTableNamePrefixes("t_");
		// 生成
		gen.generate();
	}
}
