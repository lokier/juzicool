
var document_title=document.title;$(document).ready(function()
{$("form[action='']").attr('action',window.location.href);$('.autosize').autosize();$('.aw-top-nav .navbar-toggle').click(function()
{if($(this).parents('.aw-top-nav').find('.navbar-collapse').hasClass('active'))
{$(this).parents('.aw-top-nav').find('.navbar-collapse').removeClass('active');}
else
{$(this).parents('.aw-top-nav').find('.navbar-collapse').addClass('active');}});if(typeof(G_NOTIFICATION_INTERVAL)!='undefined')
{AWS.Message.check_notifications();AWS.G.notification_timer=setInterval('AWS.Message.check_notifications()',G_NOTIFICATION_INTERVAL);}
    if($('.aw-common-list').length)
    {$.each($('.aw-common-list .aw-item.article'),function(i,e)
    {if($(this).find('.all-content img').length>=1)
    {$(this).find('.markitup-box').prepend($(this).find('.all-content img').eq(0).addClass('pull-left inline-img'))}});}
    $('a[rel=lightbox]:visible').fancybox({openEffect:'none',closeEffect:'none',prevEffect:'none',nextEffect:'none',centerOnScroll:true,closeBtn:false,helpers:{buttons:{position:'bottom'}},afterLoad:function()
        {this.title='第 '+(this.index+1)+' 张, 共 '+this.group.length+' 张'+(this.title?' - '+this.title:'');}});if(window.location.hash.indexOf('#!')!=-1)
{if($('a[name='+window.location.hash.replace('#!','')+']').length)
{$.scrollTo($('a[name='+window.location.hash.replace('#!','')+']').offset()['top']-20,600,{queue:true});}}
    AWS.show_card_box('.aw-user-name, .aw-user-img','user');AWS.show_card_box('.topic-tag, .aw-topic-name, .aw-topic-img','topic');AWS.Init.init_article_comment_box('.aw-article-content .aw-article-comment');AWS.Init.init_topic_edit_box('.aw-edit-topic');$(document).on('click','.aw-edit-topic-box .aw-dropdown-list li',function()
{$(this).parents('.aw-edit-topic-box').find('#aw_edit_topic_title').val($(this).text());$(this).parents('.aw-edit-topic-box').find('.add').click();$(this).parents('.aw-edit-topic-box').find('.aw-dropdown').hide();});$(document).on('click','.topic-tag .close',function()
{var data_type=$(this).parents('.aw-topic-bar').attr('data-type'),data_id=$(this).parents('.aw-topic-bar').attr('data-id'),data_url='',topic_id=$(this).parents('.topic-tag').attr('data-id');switch(data_type)
{case'question':data_url=G_BASE_URL+'/topic/ajax/remove_topic_relation/';break;case'topic':data_url=G_BASE_URL+'/topic/ajax/remove_related_topic/related_id-'+$(this).parents('.topic-tag').attr('data-id')+'__topic_id-'+data_id;break;case'favorite':data_url=G_BASE_URL+'/favorite/ajax/remove_favorite_tag/';break
    case'article':data_url=G_BASE_URL+'/topic/ajax/remove_topic_relation/';break;}
    if($(this).parents('.aw-topic-bar').attr('data-url'))
    {data_url=$(this).parents('.aw-topic-bar').attr('data-url');}
    if(data_type=='topic')
    {$.get(data_url);}
    else if(data_type=='favorite')
    {$.post(data_url,{'item_type':data_type,'topic_id':topic_id,'item_id':data_id,'tags':$.trim($(this).parents('.topic-tag').text())},function(result)
    {},'json');}
    else
    {$.post(data_url,{'type':data_type,'topic_id':topic_id,'item_id':data_id},function(result)
    {$('#aw-ajax-box').empty();},'json');}
    $(this).parents('.topic-tag').remove();return false;});$(document).on('mouseover','#aw-card-tips',function()
{clearTimeout(AWS.G.card_box_hide_timer);$(this).show();});$(document).on('mouseout','#aw-card-tips',function()
{$(this).hide();});$(document).on('click','.aw-card-tips-user .follow',function()
{var uid=$(this).parents('.aw-card-tips').find('.name').attr('data-id');$.each(AWS.G.cashUserData,function(i,a)
{if(a.match('data-id="'+uid+'"'))
{if(AWS.G.cashUserData.length==1)
{AWS.G.cashUserData=[];}
else
{AWS.G.cashUserData[i]='';}}});});$(document).on('click','.aw-card-tips-topic .follow',function()
{var topic_id=$(this).parents('.aw-card-tips').find('.name').attr('data-id');$.each(AWS.G.cashTopicData,function(i,a)
{if(a.match('data-id="'+topic_id+'"'))
{if(AWS.G.cashTopicData.length==1)
{AWS.G.cashTopicData=[];}
else
{AWS.G.cashTopicData[i]='';}}});});$(document).on('mouseover','.follow, .voter, .aw-icon-thank-tips, .invite-list-user',function()
{$(this).tooltip('show');});AWS.Dropdown.bind_dropdown_list('#aw-search-query','search');AWS.at_user_lists('#wmd-input, .aw-article-replay-box #comment_editor',5);if(document.all)
{AWS.check_placeholder($('input, textarea'));setInterval(function()
{AWS.check_placeholder($('input[data-placeholder!="true"], textarea[data-placeholder!="true"]'));},1000);}
    if($('.aw-back-top').length)
    {$(window).scroll(function()
    {if($(window).scrollTop()>($(window).height()/2))
    {$('.aw-back-top').fadeIn();}
    else
    {$('.aw-back-top').fadeOut();}});}});$(window).on('hashchange',function(){if(window.location.hash.indexOf('#!')!=-1)
{if($('a[name='+window.location.hash.replace('#!','')+']').length)
{$.scrollTo($('a[name='+window.location.hash.replace('#!','')+']').offset()['top']-20,600,{queue:true});}}});var AW_TEMPLATE={'loadingBox':'<div id="aw-loading" class="collapse">'+'<div id="aw-loading-box"></div>'+'</div>','loadingMiniBox':'<div id="aw-loading-mini-box"></div>','userCard':'<div id="aw-card-tips" class="aw-card-tips aw-card-tips-user">'+'<div class="aw-mod">'+'<div class="mod-head">'+'<a href="{{url}}" class="img">'+'<img src="{{avatar_file}}" alt="" />'+'</a>'+'<p class="title clearfix">'+'<a href="{{url}}" class="name pull-left" data-id="{{uid}}">{{user_name}}</a>'+'<i class="{{verified_enterprise}} pull-left" title="{{verified_title}}"></i>'+'</p>'+'<p class="aw-user-center-follow-meta">'+'<span>'+_t('威望')+': <em class="aw-text-color-green">{{reputation}}</em></span>'+'<span>'+_t('赞同')+': <em class="aw-text-color-orange">{{agree_count}}</em></span>'+'</p>'+'</div>'+'<div class="mod-body">'+'<p>{{signature}}</p>'+'</div>'+'<div class="mod-footer clearfix">'+'<span>'+'<a class="text-color-999" onclick="AWS.dialog(\'inbox\', \'{{user_name}}\');"><i class="icon icon-inbox"></i> '+_t('私信')+'</a>&nbsp;&nbsp;&nbsp;&nbsp;<a  class="text-color-999" onclick="AWS.dialog(\'publish\', {category_enable:{{category_enable}}, ask_user_id:{{uid}}, ask_user_name:{{ask_name}} });"><i class="icon icon-at"></i> '+_t('问Ta')+'</a>'+'</span>'+'<a class="btn btn-normal btn-success follow {{focus}} pull-right" onclick="AWS.User.follow($(this), \'user\', {{uid}});"><span>{{focusTxt}}</span> <em>|</em> <b>{{fansCount}}</b></a>'+'</div>'+'</div>'+'</div>','topicCard':'<div id="aw-card-tips" class="aw-card-tips aw-card-tips-topic">'+'<div class="aw-mod">'+'<div class="mod-head">'+'<a href="{{url}}" class="img">'+'<img src="{{topic_pic}}" alt="" title=""/>'+'</a>'+'<p class="title">'+'<a href="{{url}}" class="name" data-id="{{topic_id}}">{{topic_title}}</a>'+'</p>'+'<p class="desc">'+'{{topic_description}}'+'</p>'+'</div>'+'<div class="mod-footer">'+'<span>'+_t('讨论数')+': {{discuss_count}}</span>'+'<a class="btn btn-normal btn-success follow {{focus}} pull-right" onclick="AWS.User.follow($(this), \'topic\', {{topic_id}});"><span>{{focusTxt}}</span> <em>|</em> <b>{{focus_count}}</b></a>'+'</div>'+'</div>'+'</div>','alertBox':'<div class="modal fade alert-box aw-tips-box">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">'+_t('提示信息')+'</h3>'+'</div>'+'<div class="modal-body">'+'<p>{{message}}</p>'+'</div>'+'</div>'+'</div>'+'</div>','editCommentBox':'<div class="modal fade alert-box aw-edit-comment-box aw-editor-box">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">'+_t('编辑回复')+'</h3>'+'</div>'+'<form action="'+G_BASE_URL+'/question/ajax/update_answer/answer_id-{{answer_id}}" method="post" onsubmit="return false" id="answer_edit">'+'<div class="modal-body">'+'<div class="alert alert-danger collapse error_message"><i class="icon icon-delete"></i> <em></em></div>'+'<input type="hidden" name="attach_access_key" value="{{attach_access_key}}" />'+'<textarea name="answer_content" id="editor_reply" class="form-control" rows="10"></textarea>'+'<div class="aw-file-upload-box">'+'<div class="aw-upload-box">'+'<a class="btn btn-default">上传附件</a>'+'<div class="upload-container"></div>'+'</div>'+'</div>'+'</div>'+'<div class="modal-footer">'+'<span><input id="aw-do-delete" type="checkbox" value="1" name="do_delete" /><label for="aw-do-delete">'+_t('删除回复')+'</label></span>'+'<button class="btn btn-large btn-success" onclick="AWS.ajax_post($(\'#answer_edit\'), AWS.ajax_processer, \'ajax_post_alert\');return false;">'+_t('确定')+'</button>'+'</div>'+'</form>'+'</div>'+'</div>'+'</div>','articleCommentBox':'<div class="aw-article-replay-box clearfix">'+'<form action="'+G_BASE_URL+'/article/ajax/save_comment/" onsubmit="return false;" method="post">'+'<div class="mod-body">'+'<input type="hidden" name="at_uid" value="{{at_uid}}">'+'<input type="hidden" name="post_hash" value="'+G_POST_HASH+'" />'+'<input type="hidden" name="article_id" value="{{article_id}}" />'+'<textarea placeholder="'+_t('写下你的评论...')+'" class="form-control" id="comment_editor" name="message" rows="2"></textarea>'+'</div>'+'<div class="mod-footer">'+'<a href="javascript:;" onclick="AWS.ajax_post($(this).parents(\'form\'));" class="btn btn-normal btn-success pull-right btn-submit">'+_t('回复')+'</a>'+'</div>'+'</form>'+'</div>','favoriteBox':'<div class="modal collapse fade alert-box aw-favorite-box">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">'+_t('收藏')+'</h3>'+'</div>'+'<form id="favorite_form" action="'+G_BASE_URL+'/favorite/ajax/update_favorite_tag/" method="post" onsubmit="return false;">'+'<input type="hidden" name="item_id" value="{{item_id}}" />'+'<input type="hidden" name="item_type" value="{{item_type}}" />'+'<input type="text" name="tags" id="add_favorite_tags" class="collapse" />'+'<div class="mod aw-favorite-tag-list">'+'<div class="modal-body">'+'<div class="mod-body"><ul></ul></div>'+'<div class="alert alert-danger collapse error_message"><i class="icon icon-delete"></i> <em></em></div>'+'</div>'+'<div class="modal-footer">'+'<a class="pull-left" onclick="$(\'.aw-favorite-box .aw-favorite-tag-list\').hide();$(\'.aw-favorite-box .aw-favorite-tag-add\').show();">'+_t('创建标签')+'</a>'+'<a href="javascript:;"  data-dismiss="modal" aria-hidden="true" class="btn btn-large btn-gray" onclick="return false;">'+_t('关闭')+'</a>'+'</div>'+'</div>'+'<div class="mod aw-favorite-tag-add collapse">'+'<div class="modal-body">'+'<input type="text" class="form-control add-input" placeholder="'+_t('标签名字')+'" />'+'</div>'+'<div class="modal-footer">'+'<a class="text-color-999" onclick="$(\'.aw-favorite-box .aw-favorite-tag-list\').show();$(\'.aw-favorite-box .aw-favorite-tag-add\').hide();" style="margin-right:10px;">'+_t('取消')+'</a>'+'<a href="javascript:;" class="btn btn-large btn-success" onclick="AWS.User.add_favorite_tag()">'+_t('确认创建')+'</a>'+'</div>'+'</div>'+'</form>'+'</div>'+'</div>'+'</div>','questionRedirect':'<div class="modal fade alert-box aw-question-redirect-box">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">'+_t('问题重定向至')+'</h3>'+'</div>'+'<div class="modal-body">'+'<p>'+_t('将问题重定向至')+'</p>'+'<div class="aw-question-drodpwon">'+'<input id="question-input" class="form-control" type="text" data-id="{{data_id}}" placeholder="'+_t('搜索问题或问题 ID')+'" />'+'<div class="aw-dropdown"><p class="title">'+_t('没有找到相关结果')+'</p><ul class="aw-dropdown-list"></ul></div>'+'</div>'+'<p class="clearfix"><a href="javascript:;" class="btn btn-large btn-success pull-right" onclick="$(\'.alert-box\').modal(\'hide\');">'+_t('放弃操作')+'</a></p>'+'</div>'+'</div>'+'</div>'+'</div>','publishBox':'<div class="modal fade alert-box aw-publish-box">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">'+_t('发起问题')+'</h3>'+'</div>'+'<div class="modal-body">'+'<div class="alert alert-danger collapse error_message"><i class="icon icon-delete"></i> <em></em></div>'+'<form action="'+G_BASE_URL+'/publish/ajax/publish_question/" method="post" id="quick_publish" onsubmit="return false">'+'<input type="hidden" id="quick_publish_category_id" name="category_id" value="{{category_id}}" />'+'<input type="hidden" name="post_hash" value="'+G_POST_HASH+'" />'+'<input type="hidden" name="ask_user_id" value="{{ask_user_id}}" />'+'<div>'+'<textarea class="form-control" placeholder="'+_t('写下你的问题')+'..." rows="1" name="question_content" id="quick_publish_question_content" onkeydown="if (event.keyCode == 13) { return false; }"></textarea>'+'<div class="aw-publish-suggest-question collapse">'+'<p class="text-color-999">你的问题可能已经有答案</p>'+'<ul class="aw-dropdown-list">'+'</ul>'+'</div>'+'</div>'+'<textarea name="question_detail" class="form-control" rows="4" placeholder="'+_t('问题背景、条件等详细信息')+'..."></textarea>'+'<div class="aw-publish-title">'+'<div class="dropdown" id="quick_publish_category_chooser">'+'<div class="dropdown-toggle" data-toggle="dropdown">'+'<span id="aw-topic-tags-select" class="aw-hide-txt">'+_t('选择分类')+'</span>'+'<a><i class="icon icon-down"></i></a>'+'</div>'+'</div>'+'</div>'+'<div class="aw-topic-bar" data-type="publish">'+'<div class="tag-bar clearfix">'+'<span class="aw-edit-topic"><i class="icon icon-edit"></i>'+_t('编辑话题')+'</span>'+'</div>'+'</div>'+'<div class="clearfix collapse" id="quick_publish_captcha">'+'<input type="text" class="pull-left form-control" name="seccode_verify" placeholder="'+_t('验证码')+'" />'+'<img id="qp_captcha" class="pull-left" onclick="this.src = \''+G_BASE_URL+'/account/captcha/\' + Math.floor(Math.random() * 10000);" src="" />'+'</div>'+'</form>'+'</div>'+'<div class="modal-footer">'+'<span class="pull-right">'+'<a data-dismiss="modal" aria-hidden="true" class="text-color-999">'+_t('取消')+'</a>'+'<button class="btn btn-large btn-success" onclick="AWS.ajax_post($(\'#quick_publish\'), AWS.ajax_processer, \'error_message\');">'+_t('发起')+'</button>'+'</span>'+'<a href="javascript:;" tabindex="-1" onclick="$(\'form#quick_publish\').attr(\'action\', \''+G_BASE_URL+'/publish/\');$.each($(\'#quick_publish textarea\'), function (i, e){if ($(this).val() == $(this).attr(\'placeholder\')){$(this).val(\'\');}});document.getElementById(\'quick_publish\').submit();" class="pull-left">'+_t('高级模式')+'</a>'+'</div>'+'</div>'+'</div>'+'</div>','inbox':'<div class="modal fade alert-box aw-inbox">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">'+_t('新私信')+'</h3>'+'</div>'+'<div class="modal-body">'+'<div class="alert alert-danger collapse error_message"> <i class="icon icon-delete"></i> <em></em></div>'+'<form action="'+G_BASE_URL+'/inbox/ajax/send/" method="post" id="quick_publish" onsubmit="return false">'+'<input type="hidden" name="post_hash" value="'+G_POST_HASH+'" />'+'<input id="invite-input" class="form-control" type="text" placeholder="'+_t('搜索用户')+'" name="recipient" value="{{recipient}}" />'+'<div class="aw-dropdown">'+'<p class="title">'+_t('没有找到相关结果')+'</p>'+'<ul class="aw-dropdown-list">'+'</ul>'+'</div>'+'<textarea class="form-control" name="message" rows="3" placeholder="'+_t('私信内容...')+'"></textarea>'+'</form>'+'</div>'+'<div class="modal-footer">'+'<a data-dismiss="modal" aria-hidden="true" class="text-color-999">'+_t('取消')+'</a>'+'<button class="btn btn-large btn-success" onclick="AWS.ajax_post($(\'#quick_publish\'), AWS.ajax_processer, \'error_message\');">'+_t('发送')+'</button>'+'</div>'+'</div>'+'</div>'+'</div>','editTopicBox':'<div class="aw-edit-topic-box form-inline">'+'<input type="text" class="form-control" id="aw_edit_topic_title" autocomplete="off"  placeholder="'+_t('创建或搜索添加新话题')+'...">'+'<a class="btn btn-normal btn-success add">'+_t('添加')+'</a>'+'<a class="btn btn-normal btn-gray close-edit">'+_t('取消')+'</a>'+'<div class="aw-dropdown">'+'<p class="title">'+_t('没有找到相关结果')+'</p>'+'<ul class="aw-dropdown-list">'+'</ul>'+'</div>'+'</div>','ajaxData':'<div class="modal fade alert-box aw-topic-edit-note-box aw-question-edit" aria-labelledby="myModalLabel" role="dialog">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">{{title}}</h3>'+'</div>'+'<div class="modal-body">'+'{{data}}'+'</div>'+'</div>'+'</div>'+'</div>','commentBox':'<div class="aw-comment-box" id="{{comment_form_id}}">'+'<div class="aw-comment-list"><p align="center" class="aw-padding10"><i class="aw-loading"></i></p></div>'+'<form action="{{comment_form_action}}" method="post" onsubmit="return false">'+'<div class="aw-comment-box-main">'+'<textarea class="aw-comment-txt form-control" rows="2" name="message" placeholder="'+_t('评论一下')+'..."></textarea>'+'<div class="aw-comment-box-btn">'+'<span class="pull-right">'+'<a href="javascript:;" class="btn btn-mini btn-success" onclick="AWS.User.save_comment($(this));">'+_t('评论')+'</a>'+'<a href="javascript:;" class="btn btn-mini btn-gray close-comment-box">'+_t('取消')+'</a>'+'</span>'+'</div>'+'</div>'+'</form>'+'</div>','commentBoxClose':'<div class="aw-comment-box" id="{{comment_form_id}}">'+'<div class="aw-comment-list"><p align="center" class="aw-padding10"><i class="aw-loading"></i></p></div>'+'</div>','dropdownList':'<div aria-labelledby="dropdownMenu" role="menu" class="aw-dropdown">'+'<ul class="aw-dropdown-list">'+'{{#items}}'+'<li><a data-value="{{id}}">{{title}}</a></li>'+'{{/items}}'+'</ul>'+'</div>','reportBox':'<div class="modal fade alert-box aw-share-box aw-share-box-message aw-report-box" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">'+_t('举报问题')+'</h3>'+'</div>'+'<form id="quick_publish" method="post" action="'+G_BASE_URL+'/question/ajax/save_report/">'+'<input type="hidden" name="type" value="{{item_type}}" />'+'<input type="hidden" name="target_id" value="{{item_id}}" />'+'<div class="modal-body">'+'<div class="dropdown reportBox" style="height: 34px;line-height: 34px;border: 1px solid #ccc;cursor: pointer;border-radius: 4px;">'+'<div class="dropdown" data-toggle="dropdown" ><span id="aw-report-tags-select" class="aw-hide-txt">选择举报理由</span><a style="float: right;" href="javascript:;"><i class="icon icon-down"></i></a></div>'+'<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel" style="width: 100%;">'+'{{#item_reson}}'+'<li value="{{.}}"><a href="javascript:void(0);">{{.}}</a></li>'+'{{/item_reson}}'+'</ul>'+'</div>'+'<div class="alert alert-danger collapse error_message"><i class="icon icon-delete"></i> <em></em></div>'+'<textarea class="form-control" name="reason" rows="5" placeholder="'+_t('请填写举报理由')+'..."></textarea>'+'</div>'+'<div class="modal-footer">'+'<a data-dismiss="modal" aria-hidden="true" class="text-color-999">'+_t('取消')+'</a>'+'<button class="btn btn-large btn-success" onclick="AWS.ajax_post($(\'#quick_publish\'), AWS.ajax_processer, \'error_message\');return false;">'+_t('提交')+'</button>'+'</div>'+'</form>'+'</div>'+'</div>'+'</div>','recommend':'<div class="modal fade alert-box aw-recommend-box">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">'+_t('推荐到帮助中心')+'</h3>'+'</div>'+'<form id="help_form" action="'+G_BASE_URL+'/help/ajax/add_data/" method="post" onsubmit="return false;">'+'<input type="hidden" name="item_id" value="{{item_id}}" />'+'<input type="hidden" name="item_type" value="{{question}}" />'+'<input type="hidden" name="item_type" value="{{article}}" />'+'<div class="mod">'+'<div class="modal-body clearfix">'+'<div class="alert alert-danger collapse error_message"><i class="icon icon-delete"></i> <em></em></div>'+'<div class="mod-body">'+'<ul></ul>'+'</div>'+'</div>'+'</div>'+'<div class="modal-footer">'+'<button href="javascript:;"  data-dismiss="modal" aria-hidden="true" class="btn btn-normal btn-gray">'+_t('关闭')+'</button>'+'</div>'+'</form>'+'</div>'+'</div>'+'</div>','searchDropdownListQuestions':'<li class="{{active}} question clearfix"><i class="icon icon-bestbg pull-left"></i><a class="aw-hide-txt pull-left" href="{{url}}">{{content}} </a><span class="pull-right text-color-999">{{discuss_count}} '+_t('个回复')+'</span></li>','searchDropdownListTopics':'<li class="topic clearfix"><span class="topic-tag" data-id="{{topic_id}}"><a href="{{url}}" class="text">{{name}}</a></span> <span class="pull-right text-color-999">{{discuss_count}} '+_t('个讨论')+'</span></li>','searchDropdownListUsers':'<li class="user clearfix"><a href="{{url}}"><img src="{{img}}" />{{name}}<span class="aw-hide-txt">{{intro}}</span></a></li>','searchDropdownListArticles':'<li class="question clearfix"><a class="aw-hide-txt pull-left" href="{{url}}">{{content}} </a><span class="pull-right text-color-999">{{comments}} '+_t('条评论')+'</span></li>','inviteDropdownList':'<li class="user"><a data-url="{{url}}" data-id="{{uid}}" data-actions="{{action}}" data-value="{{name}}"><img class="img" src="{{img}}" />{{name}}</a></li>','editTopicDorpdownList':'<li class="question"><a>{{name}}</a></li>','questionRedirectList':'<li class="question"><a class="aw-hide-txt" onclick="AWS.ajax_request({{url}})">{{name}}</a></li>','questionDropdownList':'<li class="question" data-id="{{id}}"><a class="aw-hide-txt" href="{{url}}">{{name}}</a></li>','inviteUserList':'<li>'+'<a class="pull-right btn btn-mini btn-default" onclick="disinvite_user($(this),{{uid}});$(this).parent().detach();">'+_t('取消邀请')+'</a>'+'<a class="aw-user-name" data-id="{{uid}}">'+'<img src="{{img}}" alt="" />'+'</a>'+'<span class="aw-text-color-666">{{name}}</span>'+'</li>','educateInsert':'<td class="e1" data-txt="{{school}}">{{school}}</td>'+'<td class="e2" data-txt="{{departments}}">{{departments}}</td>'+'<td class="e3" data-txt="{{year}}">{{year}} '+_t('年')+'</td>'+'<td><a class="delete-educate">'+_t('删除')+'</a>&nbsp;&nbsp;<a class="edit-educate">'+_t('编辑')+'</a></td>','educateEdit':'<td><input type="text" value="{{school}}" class="school form-control"></td>'+'<td><input type="text" value="{{departments}}" class="departments form-control"></td>'+'<td><select class="year edityear">'+'</select> '+_t('年')+'</td>'+'<td><a class="delete-educate">'+_t('删除')+'</a>&nbsp;&nbsp;<a class="add-educate">'+_t('保存')+'</a></td>','workInsert':'<td class="w1" data-txt="{{company}}">{{company}}</td>'+'<td class="w2" data-txt="{{jobid}}">{{work}}</td>'+'<td class="w3" data-s-val="{{syear}}" data-e-val="{{eyear}}">{{syear}} '+_t('年')+' '+_t('至')+' {{eyear}}</td>'+'<td><a class="delete-work">'+_t('删除')+'</a>&nbsp;&nbsp;<a class="edit-work">'+_t('编辑')+'</a></td>','workEidt':'<td><input type="text" value="{{company}}" class="company form-control"></td>'+'<td>'+'<select class="work editwork">'+'</select>'+'</td>'+'<td><select class="syear editsyear">'+'</select>&nbsp;&nbsp;'+_t('年')+' &nbsp;&nbsp; '+_t('至')+'&nbsp;&nbsp;&nbsp;&nbsp;'+'<select class="eyear editeyear">'+'</select> '+_t('年')+'</td>'+'<td><a class="delete-work">'+_t('删除')+'</a>&nbsp;&nbsp;<a class="add-work">'+_t('保存')+'</a></td>','alertImg':'<div class="modal fade alert-box aw-tips-box aw-alert-img-box">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">'+_t('提示信息')+'</h3>'+'</div>'+'<div class="modal-body">'+'<p class="hide {{hide}}">{{message}}</p>'+'<img src="{{url}}" />'+'</div>'+'</div>'+'</div>'+'</div>','confirmBox':'<div class="modal fade alert-box aw-confirm-box">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="modal-header">'+'<a type="button" class="close icon icon-delete" data-dismiss="modal" aria-hidden="true"></a>'+'<h3 class="modal-title" id="myModalLabel">'+_t('提示信息')+'</h3>'+'</div>'+'<div class="modal-body">'+'{{message}}'+'</div>'+'<div class="modal-footer">'+'<a class="btn btn-gray" data-dismiss="modal" aria-hidden="true">取消</a>'+'<a class="btn btn-success yes">确定</a>'+'</div>'+'</div>'+'</div>'+'</div>','ProjectForm':'<div class="mod aw-project-return-form collapse">'+'<form action="" method="" name="">'+'<div class="mod-body">'+'<dl class="clearfix">'+'<dt><strong>*</strong>回报标题:</dt>'+'<dd><input type="text" class="form-control form-normal title"/><label class="label label-danger collapse">回报标题与支持额度至少填写一个</label></dd>'+'</dl>'+'<dl>'+'<dt><strong>*</strong>支持额度:</dt>'+'<dd><input type="text" class="form-control form-normal amount" name="" /> <label class="label label-danger collapse">额度不能为空</label></dd>'+'</dl>'+'<dl class="clearfix">'+'<dt><strong>*</strong>回报内容:</dt>'+'<dd>'+'<textarea rows="5" class="form-control content"></textarea> <label class="label label-danger collapse">回报内容不能为空</label>'+'</dd>'+'</dl>'+'<dl>'+'<dt><strong>*</strong>限定名额:</dt>'+'<dd>'+'<label>'+'<input type="radio" name="limit-num" class="limit-num-no" value="false" checked="checked" /> 否 '+'</label>'+'<label>'+'<input type="radio" name="limit-num" class="limit-num-yes" value="true"/> 是 '+'</label>'+'<label class="count collapse">'+'<span class="pull-left">名额数量:</span>'+'<input type="text" class="form-control form-xs pull-left people-amount" name="" />'+'</label>'+'</dd>'+'</dl>'+'<dl>'+'<dt></dt>'+'<dd>'+'<a href="javascript:;" class="btn btn-primary btn-green save">保存</a>'+'<a href="javascript:;" class="btn btn-default cancel">取消</a>'+'</dd>'+'</dl>'+'</div>'+'</form>'+'</div>','activityBox':'<div class="modal fade alert-box aw-topic-edit-note-box aw-question-edit" aria-labelledby="myModalLabel" role="dialog">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="kn-box vmod aw-publish-contact">'+'<label class="label label-danger collapse"></label>'+'<div class="mod-head">'+'<p>'+'提示：提交审核后点名时间将在 3 个工作日内完成审核，请留意站内通知以及你的邮箱'+'</p>'+'</div>'+'<div class="mod-body">'+'<dl>'+'<dt><strong>*</strong>姓名:</dt>'+'<dd>'+'<input type="text" id="publish-name" class="form-control form-normal" name="contact[name]" value="{{contact_name}}" />'+'</dd>'+'</dl>'+'<dl>'+'<dt><strong>*</strong>手机:</dt>'+'<dd>'+'<input type="text" id="publish-tel" class="form-control form-normal" name="contact[mobile]" value="{{contact_tel}}" />'+'</dd>'+'</dl>'+'<dl>'+'<dt><strong>*</strong>QQ:</dt>'+'<dd>'+'<input type="text" id="publish-qq" class="form-control form-normal" name="contact[qq]" value="{{contact_qq}}" />'+'</dd>'+'</dl>'+'</div>'+'<div class="mod-footer">'+'<a class="btn btn-normal btn-success" >'+'提交审核 '+'</a>'+'</div>'+'</div>'+'</div>'+'</div>'+'</div>','projectEventForm':'<div class="modal fade alert-box aw-topic-edit-note-box aw-question-edit" aria-labelledby="myModalLabel" role="dialog">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="formBox">'+'<div class="title">'+'<h3>活动报名 <i class="icon icon-delete pull-right" data-dismiss="modal" aria-hidden="true"></i></h3>'+'</div>'+'<div class="main ">'+'<form class="form-horizontal" action="'+G_BASE_URL+'/project/ajax/add_product_order/" onsubmit="return false" role="form" id="projectEventForm" method="post">'+'<input type="hidden" name="project_id" value="{{project_id}}">'+' <div class="form-group">'+'<label class="col-sm-4 control-label">真实姓名:</label>'+' <div class="col-sm-7">'+' <input type="text" class="form-control" name="name" value="{{contact_name}}" placeholder="'+_t('请务必实名')+'" >'+' </div>'+'</div>'+' <div class="form-group">'+'  <label  class="col-sm-4 control-label">手机:</label>'+'<div class="col-sm-7">'+'<input type="text" class="form-control" name="mobile" value="{{contact_tel}}" >'+' </div>'+' </div>'+' <div class="form-group">'+' <label  class="col-sm-4 control-label">邮箱:</label>'+' <div class="col-sm-7">'+'<input type="text" class="form-control" name="email" value="{{contact_email}}" >'+'</div>'+' </div>'+' <div class="form-group">'+' <label  class="col-sm-4 control-label">地址:</label>'+' <div class="col-sm-7">'+'<input type="text" class="form-control" name="address" value="{{contact_address}}" placeholder="'+_t('完整收件地址')+'" >'+'</div>'+' </div>'+'</form>'+'</div>'+'<div class="footer pull-right">'+'<a onclick="AWS.ajax_post($(\'#projectEventForm\'));">确定</a>'+'</div>'+'</div>'+'</div>'+'</div>'+'</div>','projectStockForm':'<div class="modal fade alert-box aw-topic-edit-note-box aw-question-edit" aria-labelledby="myModalLabel" role="dialog">'+'<div class="modal-dialog">'+'<div class="modal-content">'+'<div class="formBox">'+'<div class="title">'+'<h3>预约投资 <i class="icon icon-delete pull-right" data-dismiss="modal" aria-hidden="true"></i></h3>'+'</div>'+'<div class="main ">'+'<form class="form-horizontal" action="'+G_BASE_URL+'/project/ajax/add_product_order/" onsubmit="return false" role="form" id="projectEventForm" method="post">'+'<input type="hidden" name="project_id" value="{{project_id}}">'+' <div class="form-group">'+'<label  class="col-sm-4 control-label">预计投资:</label>'+' <div class="col-sm-7">'+' <input  type="text" class="form-control" name="amount" value="{{contact_money}}">'+'</div>'+' </div>'+' <div class="form-group">'+'<label class="col-sm-4 control-label">真实姓名:</label>'+' <div class="col-sm-7">'+' <input type="text" class="form-control" name="name" value="{{contact_name}}">'+' </div>'+'</div>'+' <div class="form-group">'+'  <label  class="col-sm-4 control-label">手机:</label>'+'<div class="col-sm-7">'+'<input type="text" class="form-control" name="mobile" value="{{contact_tel}}">'+' </div>'+' </div>'+' <div class="form-group">'+' <label  class="col-sm-4 control-label">邮箱:</label>'+' <div class="col-sm-7">'+'<input type="text" class="form-control" name="email" value="{{contact_email}}" >'+'</div>'+' </div>'+'</form>'+'</div>'+'<div class="footer pull-right">'+'<a onclick="ajax_post($(\'#projectEventForm\'));">确定</a>'+'</div>'+'</div>'+'</div>'+'</div>'+'</div>'}
var AWS={loading:function(type)
    {if(!$('#aw-loading').length)
    {$('#aw-ajax-box').append(AW_TEMPLATE.loadingBox);}
        if(type=='show')
        {if($('#aw-loading').css('display')=='block')
        {return false;}
            $('#aw-loading').fadeIn();AWS.G.loading_timer=setInterval(function()
        {AWS.G.loading_bg_count-=1;$('#aw-loading-box').css('background-position','0px '+AWS.G.loading_bg_count*40+'px');if(AWS.G.loading_bg_count==1)
        {AWS.G.loading_bg_count=12;}},100);}
        else
        {$('#aw-loading').fadeOut();clearInterval(AWS.G.loading_timer);}},loading_mini:function(selector,type)
    {if(!selector.find('#aw-loading-mini-box').length)
    {selector.append(AW_TEMPLATE.loadingMiniBox);}
        if(type=='show')
        {selector.find('#aw-loading-mini-box').fadeIn();AWS.G.loading_timer=setInterval(function()
        {AWS.G.loading_mini_bg_count-=1;$('#aw-loading-mini-box').css('background-position','0px '+AWS.G.loading_mini_bg_count*16+'px');if(AWS.G.loading_mini_bg_count==1)
        {AWS.G.loading_mini_bg_count=9;}},100);}
        else
        {selector.find('#aw-loading-mini-box').fadeOut();clearInterval(AWS.G.loading_timer);}},ajax_request:function(url,params)
    {AWS.loading('show');if(params)
    {$.post(url,params+'&_post_type=ajax',function(result)
    {_callback(result);},'json').error(function(error)
    {_error(error);});}
    else
    {$.get(url,function(result)
    {_callback(result);},'json').error(function(error)
    {_error(error);});}
        function _callback(result)
        {AWS.loading('hide');if(!result)
        {return false;}
            if(result.err)
            {AWS.alert(result.err);}
            else if(result.rsm&&result.rsm.url)
            {window.location=decodeURIComponent(result.rsm.url);}
            else if(result.errno==1)
            {window.location.reload();}}
        function _error(error)
        {AWS.loading('hide');if($.trim(error.responseText)!='')
        {alert(_t('发生错误, 返回的信息:')+' '+error.responseText);}}
        return false;},ajax_post:function(formEl,processer,type)
    {if(typeof CKEDITOR!='undefined')
    {for(instance in CKEDITOR.instances){CKEDITOR.instances[instance].updateElement();}}
        if(typeof(processer)!='function')
        {var processer=AWS.ajax_processer;AWS.loading('show');}
        if(!type)
        {var type='default';}
        else if(type=='reply_question')
        {AWS.loading('show');$('.btn-reply').addClass('disabled');if(EDITOR!=undefined)
        {EDITOR.removeListener('blur',EDITOR_CALLBACK);}}
        var custom_data={_post_type:'ajax'};formEl.ajaxSubmit({dataType:'json',data:custom_data,success:function(result)
        {processer(type,result);},error:function(error)
        {console.log(error);if($.trim(error.responseText)!='')
        {AWS.loading('hide');alert(_t('发生错误, 返回的信息:')+' '+error.responseText);}
        else if(error.status==0)
        {AWS.loading('hide');alert(_t('网络链接异常'));}
        else if(error.status==500)
        {AWS.loading('hide');alert(_t('内部服务器错误'));}}});},ajax_processer:function(type,result)
    {AWS.loading('hide');if(typeof(result.errno)=='undefined')
    {AWS.alert(result);}
    else if(result.errno!=1)
    {switch(type)
    {case'default':case'comments_form':case'reply':case'reply_question':AWS.alert(result.err);$('.aw-comment-box-btn .btn-success, .btn-reply').removeClass('disabled');break;case'ajax_post_alert':case'ajax_post_modal':case'error_message':if(!$('.error_message').length)
        {alert(result.err);}
        else if($('.error_message em').length)
        {$('.error_message em').html(result.err);}
        else
        {$('.error_message').html(result.err);}
            if($('.error_message').css('display')!='none')
            {AWS.shake($('.error_message'));}
            else
            {$('.error_message').fadeIn();}
            if($('#captcha').length)
            {$('#captcha').click();}
            break;}}
    else
    {if(type=='comments_form')
    {AWS.reload_comments_list(result.rsm.item_id,result.rsm.item_id,result.rsm.type_name);$('#aw-comment-box-'+result.rsm.type_name+'-'+result.rsm.item_id+' form textarea').val('');$('.aw-comment-box-btn .btn-success').removeClass('disabled');}
        if(result.rsm&&result.rsm.url)
        {if(window.location.href==result.rsm.url)
        {window.location.reload();}
        else
        {window.location=decodeURIComponent(result.rsm.url);}}
        else
        {switch(type)
        {case'default':case'ajax_post_alert':case'error_message':window.location.reload();break;case'ajax_post_modal':$('#aw-ajax-box div.modal').modal('hide');break;case'reply_question':AWS.loading('hide');if(result.rsm.ajax_html)
            {$('.aw-feed-list').append(result.rsm.ajax_html);$('.aw-comment-box-btn .btn-success, .btn-reply').removeClass('disabled');$.scrollTo($('#'+$(result.rsm.ajax_html).attr('id')),600,{queue:true});$('.question_answer_form').detach();if($('.aw-replay-box.question').length)
            {if(USER_ANSWERED)
            {$('.aw-replay-box').append('<p align="center">一个问题只能回复一次, 你可以在发言后 '+ANSWER_EDIT_TIME+' 分钟内编辑回复过的内容</p>');}}}
            else if(result.rsm.url)
            {window.location=decodeURIComponent(result.rsm.url);}
            else
            {window.location.reload();}
                break;case'reply':AWS.loading('hide');if(result.rsm.ajax_html)
            {$('.aw-feed-list').append(result.rsm.ajax_html);$('.aw-comment-box-btn .btn-success, .btn-reply').removeClass('disabled');$.scrollTo($('#'+$(result.rsm.ajax_html).attr('id')),600,{queue:true});$('#comment_editor').val('');}
            else if(result.rsm.url)
            {window.location=decodeURIComponent(result.rsm.url);}
            else
            {window.location.reload();}
                break;}}}},load_list_view:function(url,selector,container,start_page,callback)
    {if(!selector.attr('id'))
    {return false;}
        if(!start_page)
        {start_page=0}
        if(selector.attr('data-page')==undefined)
        {selector.attr('data-page',start_page);}
        else
        {selector.attr('data-page',parseInt(selector.attr('data-page'))+1);}
        selector.bind('click',function()
        {var _this=this;$(this).addClass('loading');$.get(url+'__page-'+$(_this).attr('data-page'),function(result)
        {$(_this).removeClass('loading');if($.trim(result)!='')
        {if($(_this).attr('data-page')==start_page&&$(_this).attr('auto-load')!='false')
        {container.html(result);}
        else
        {container.append(result);}
            $(_this).attr('data-page',parseInt($(_this).attr('data-page'))+1);}
        else
        {if($(_this).attr('data-page')==start_page&&$(_this).attr('auto-load')!='false')
        {container.html('<p style="padding: 15px 0" align="center">'+_t('没有内容')+'</p>');}
            $(_this).addClass('disabled').unbind('click').bind('click',function(){return false;});$(_this).find('span').html(_t('没有更多了'));}
            if(callback!=null)
            {callback();}});return false;});if(selector.attr('auto-load')!='false')
    {selector.click();}},reload_comments_list:function(item_id,element_id,type_name)
    {$('#aw-comment-box-'+type_name+'-'+element_id+' .aw-comment-list').html('<p align="center" class="aw-padding10"><i class="aw-loading"></i></p>');$.get(G_BASE_URL+'/question/ajax/get_'+type_name+'_comments/'+type_name+'_id-'+item_id,function(data)
    {$('#aw-comment-box-'+type_name+'-'+element_id+' .aw-comment-list').html(data);});},alert:function(text)
    {if($('.alert-box').length)
    {$('.alert-box').remove();}
        $('#aw-ajax-box').append(Hogan.compile(AW_TEMPLATE.alertBox).render({message:text}));$(".alert-box").modal('show');},dialog:function(type,data,callback)
    {switch(type)
    {case'alertImg':var template=Hogan.compile(AW_TEMPLATE.alertImg).render({'hide':data.hide,'url':data.url,'message':data.message});break;case'publish':var template=Hogan.compile(AW_TEMPLATE.publishBox).render({'category_id':data.category_id,'ask_user_id':data.ask_user_id});break;case'redirect':var template=Hogan.compile(AW_TEMPLATE.questionRedirect).render({'data_id':data});break;case'commentEdit':var template=Hogan.compile(AW_TEMPLATE.editCommentBox).render({'answer_id':data.answer_id,'attach_access_key':data.attach_access_key});break;case'favorite':var template=Hogan.compile(AW_TEMPLATE.favoriteBox).render({'item_id':data.item_id,'item_type':data.item_type});break;case'inbox':var template=Hogan.compile(AW_TEMPLATE.inbox).render({'recipient':data});break;case'report':var reson=data.item_reson.split(',');var template=Hogan.compile(AW_TEMPLATE.reportBox,'').render({'item_type':data.item_type,'item_id':data.item_id,'item_reson':reson,});break;case'topicEditHistory':var template=AW_TEMPLATE.ajaxData.replace('{{title}}',_t('编辑记录')).replace('{{data}}',data);break;case'ajaxData':var template=AW_TEMPLATE.ajaxData.replace('{{title}}',data.title).replace('{{data}}','<div id="aw_dialog_ajax_data"></div>');break;case'imagePreview':var template=AW_TEMPLATE.ajaxData.replace('{{title}}',data.title).replace('{{data}}','<p align="center"><img src="'+data.image+'" alt="" style="max-width:520px" /></p>');break;case'confirm':var template=Hogan.compile(AW_TEMPLATE.confirmBox).render({'message':data.message});break;case'recommend':var template=Hogan.compile(AW_TEMPLATE.recommend).render();break;case'projectEventForm':var template=Hogan.compile(AW_TEMPLATE.projectEventForm).render({'project_id':data.project_id,'contact_name':data.contact_name,'contact_tel':data.contact_tel,'contact_email':data.contact_email});break;case'projectStockForm':var template=Hogan.compile(AW_TEMPLATE.projectStockForm).render({'project_id':data.project_id,'contact_name':data.contact_name,'contact_tel':data.contact_tel,'contact_email':data.contact_email});break;case'activityBox':var template=Hogan.compile(AW_TEMPLATE.activityBox).render({'contact_name':data.contact_name,'contact_tel':data.contact_tel,'contact_qq':data.contact_qq});break;}
        if(template)
        {if($('.alert-box').length)
        {$('.alert-box').remove();}
            $('#aw-ajax-box').html(template).show();switch(type)
        {case'redirect':AWS.Dropdown.bind_dropdown_list($('.aw-question-redirect-box #question-input'),'redirect');break;case'inbox':AWS.Dropdown.bind_dropdown_list($('.aw-inbox #invite-input'),'inbox');$(document).on('click','.aw-inbox .aw-dropdown-list li a',function(){$('.alert-box #quick_publish input.form-control').val($(this).text());$(this).parents('.aw-dropdown').hide();});break;case'publish':AWS.Dropdown.bind_dropdown_list($('.aw-publish-box #quick_publish_question_content'),'publish');AWS.Dropdown.bind_dropdown_list($('.aw-publish-box #aw_edit_topic_title'),'topic');if(parseInt(data.category_enable)==1)
            {$.get(G_BASE_URL+'/publish/ajax/fetch_question_category/',function(result)
            {AWS.Dropdown.set_dropdown_list('.aw-publish-box .dropdown',eval(result),data.category_id);$('.aw-publish-title .dropdown li a').click(function()
            {$('.aw-publish-box #quick_publish_category_id').val($(this).attr('data-value'));$('.aw-publish-box #aw-topic-tags-select').html($(this).text());});});}
            else
            {$('.aw-publish-box .aw-publish-title').hide();}
                if(data.ask_user_id!=''&&data.ask_user_id!=undefined)
                {$('.aw-publish-box .modal-title').html('向 '+data.ask_user_name+' 提问');}
                if($('#aw-search-query').val()&&$('#aw-search-query').val()!=$('#aw-search-query').attr('placeholder'))
                {$('#quick_publish_question_content').val($('#aw-search-query').val());}
                AWS.Init.init_topic_edit_box('#quick_publish .aw-edit-topic');$('#quick_publish .aw-edit-topic').click();$('#quick_publish .close-edit').hide();if(data.topic_title)
                {$('#quick_publish .aw-edit-topic').parents('.aw-topic-bar').prepend('<span class="topic-tag"><a class="text">'+data.topic_title+'</a><a class="close" onclick="$(this).parents(\'.topic-tag\').detach();"><i class="icon icon-delete"></i></a><input type="hidden" value="'+data.topic_title+'" name="topics[]" /></span>')}
                if(typeof(G_QUICK_PUBLISH_HUMAN_VALID)!='undefined')
                {$('#quick_publish_captcha').show();$('#captcha').click();}
                break;case'favorite':$.get(G_BASE_URL+'/favorite/ajax/get_favorite_tags/',function(result)
            {var html=''
                $.each(result,function(i,e)
                {html+='<li><a data-value="'+e['title']+'"><span class="title">'+e['title']+'</span></a><i class="icon icon-followed"></i></li>';});$('.aw-favorite-tag-list ul').append(html);$.post(G_BASE_URL+'/favorite/ajax/get_item_tags/',{'item_id':$('#favorite_form input[name="item_id"]').val(),'item_type':$('#favorite_form input[name="item_type"]').val()},function(result)
            {if(result!=null)
            {$.each(result,function(i,e)
            {var index=i;$.each($('.aw-favorite-tag-list ul li .title'),function(i,e)
            {if($(this).text()==result[index])
            {$(this).parents('li').addClass('active');}});});}},'json');$(document).on('click','.aw-favorite-tag-list ul li a',function()
            {var _this=this,addClassFlag=true,url=G_BASE_URL+'/favorite/ajax/update_favorite_tag/';if($(this).parents('li').hasClass('active'))
            {url=G_BASE_URL+'/favorite/ajax/remove_favorite_tag/';addClassFlag=false;}
                $.post(url,{'item_id':$('#favorite_form input[name="item_id"]').val(),'item_type':$('#favorite_form input[name="item_type"]').val(),'tags':$(_this).attr('data-value')},function(result)
                {if(result.errno==1)
                {if(addClassFlag)
                {$(_this).parents('li').addClass('active');}
                else
                {$(_this).parents('li').removeClass('active');}}},'json');});},'json');break;case'report':$('.aw-report-box ul.dropdown-menu li').click(function()
            {$("#aw-report-tags-select").text($(this).attr('value'));$('.aw-report-box textarea').text($(this).attr('value'));});break;case'commentEdit':$.get(G_BASE_URL+'/question/ajax/fetch_answer_data/'+data.answer_id,function(result)
            {$('#editor_reply').html(result.answer_content.replace('&amp;','&'));var editor=CKEDITOR.replace('editor_reply');setTimeout(editor.on('change',function(e){var a=e.editor.document;var b=a.find("img");var count=b.count();for(var i=0;i<count;i++){var src=b.getItem(i).$.src;if(src.substring(0,10)=='data:image'){var img1=src.split(',')[1];var img2=window.atob(img1);$.ajax({type:"POST",url:G_BASE_URL+'/publish/ajax/paste/',async:false,data:{data:img1},dataType:'json',success:function(json){var imgurl=json.path;console.log(json);b.getItem(i).$.src=imgurl;var a=editor.document.$.getElementsByTagName("img")[i];a.setAttribute('data-cke-saved-src',imgurl);}});}}}),400);if(UPLOAD_ENABLE=='Y')
            {var fileupload=new FileUpload('file','.aw-edit-comment-box .aw-upload-box .btn','.aw-edit-comment-box .aw-upload-box .upload-container',G_BASE_URL+'/publish/ajax/attach_upload/id-answer__attach_access_key-'+ATTACH_ACCESS_KEY,{'insertTextarea':'.aw-edit-comment-box #editor_reply','editor':editor});$.post(G_BASE_URL+'/publish/ajax/answer_attach_edit_list/','answer_id='+data.answer_id,function(data){if(data['err']){return false;}else{$.each(data['rsm']['attachs'],function(i,v){fileupload.setFileList(v);});}},'json');}
            else
            {$('.aw-edit-comment-box .aw-file-upload-box').hide();}},'json');break;case'ajaxData':$.get(data.url,function(result){$('#aw_dialog_ajax_data').html(result);});break;case'confirm':$('.aw-confirm-box .yes').click(function()
            {if(callback)
            {callback();}
                $(".alert-box").modal('hide');return false;});break;case'recommend':$.get(G_BASE_URL+'/help/ajax/list/',function(result)
            {if(result&&result!=0)
            {var html='';$.each(result,function(i,e)
            {html+='<li class="aw-border-radius-5"><img class="aw-border-radius-5" src="'+e.icon+'"><a data-id="'+e.id+'" class="aw-hide-txt">'+e.title+'</a><i class="icon icon-followed"></i></li>'});$('.aw-recommend-box ul').append(html);$.each($('.aw-recommend-box ul li'),function(i,e)
            {if(data.focus_id==$(this).find('a').attr('data-id'))
            {$(this).addClass('active');}});$(document).on('click','.aw-recommend-box ul li a',function()
            {var _this=$(this),url=G_BASE_URL+'/help/ajax/add_data/',removeClass=false;if($(this).parents('li').hasClass('active'))
            {url=G_BASE_URL+'/help/ajax/remove_data/';removeClass=true;}
                $.post(url,{'item_id':data.item_id,'id':_this.attr('data-id'),'title':_this.text(),'type':data.type},function(result)
                {if(result.errno==1)
                {if(removeClass)
                {_this.parents('li').removeClass('active');}
                else
                {$('.aw-recommend-box ul li').removeClass('active');_this.parents('li').addClass('active');}}},'json');});}
            else
            {$('.error_message').html(_t('请先去后台创建好章节'));if($('.error_message').css('display')!='none')
            {AWS.shake($('.error_message'));}
            else
            {$('.error_message').fadeIn();}}},'json');break;}
            $(".alert-box").modal('show');}},check_placeholder:function(selector)
    {$.each(selector,function()
    {if(typeof($(this).attr("placeholder"))!="undefined")
    {$(this).attr('data-placeholder','true');if($(this).val()=='')
    {$(this).addClass('aw-placeholder').val($(this).attr("placeholder"));}
        $(this).focus(function(){if($(this).val()==$(this).attr('placeholder'))
        {$(this).removeClass('aw-placeholder').val('');}});$(this).blur(function(){if($(this).val()=='')
    {$(this).addClass('aw-placeholder').val($(this).attr('placeholder'));}});}});},hightlight:function(selector,class_name)
    {if(selector.hasClass(class_name))
    {return true;}
        var hightlight_timer_front=setInterval(function()
        {selector.addClass(class_name);},500);var hightlight_timer_background=setInterval(function()
    {selector.removeClass(class_name);},600);setTimeout(function()
    {clearInterval(hightlight_timer_front);clearInterval(hightlight_timer_background);selector.addClass(class_name);},1200);setTimeout(function()
    {selector.removeClass(class_name);},6000);},nl2br:function(str)
    {return str.replace(new RegExp("\r\n|\n\r|\r|\n","g"),"<br />");},content_switcher:function(hide_el,show_el)
    {hide_el.hide();show_el.fadeIn();},htmlspecialchars:function(text)
    {return text.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#039;');},show_card_box:function(selector,type,time)
    {if(!time)
    {var time=300;}
        $(document).on('mouseover',selector,function()
        {clearTimeout(AWS.G.card_box_hide_timer);var _this=$(this);AWS.G.card_box_show_timer=setTimeout(function()
        {if(_this.attr('data-id'))
        {switch(type)
        {case'user':if(AWS.G.cashUserData.length==0)
            {_getdata('user','/people/ajax/user_info/uid-');}
            else
            {var flag=0;_checkcash('user');if(flag==0)
            {_getdata('user','/people/ajax/user_info/uid-');}}
                break;case'topic':if(AWS.G.cashTopicData.length==0)
            {_getdata('topic','/topic/ajax/topic_info/topic_id-');}
            else
            {var flag=0;_checkcash('topic');if(flag==0)
            {_getdata('topic','/topic/ajax/topic_info/topic_id-');}}
                break;}}
            function _getdata(type,url)
            {if(type=='user')
            {$.get(G_BASE_URL+url+_this.attr('data-id'),function(result)
            {var focus=result.focus,verified=result.verified,focusTxt;if(focus==1)
            {focus='active';focusTxt='取消关注';}
            else
            {focus='';focusTxt='关注';}
                if(result.verified=='enterprise')
                {verified_enterprise='icon-v i-ve';verified_title='企业认证';}
                else if(result.verified=='personal')
                {verified_enterprise='icon-v';verified_title='个人认证';}
                else
                {verified_enterprise=verified_title='';}
                $('#aw-ajax-box').html(Hogan.compile(AW_TEMPLATE.userCard).render({'verified_enterprise':verified_enterprise,'verified_title':verified_title,'uid':result.uid,'avatar_file':result.avatar_file,'user_name':result.user_name,'reputation':result.reputation,'agree_count':result.agree_count,'signature':result.signature,'url':result.url,'category_enable':result.category_enable,'focus':focus,'focusTxt':focusTxt,'ask_name':"'"+result.user_name+"'",'fansCount':result.fans_count}));if(G_USER_ID==''||G_USER_ID==result.uid||result.uid<0)
            {$('#aw-card-tips .mod-footer').hide();}
                _init();AWS.G.cashUserData.push($('#aw-ajax-box').html());},'json');}
                if(type=='topic')
                {$.get(G_BASE_URL+url+_this.attr('data-id'),function(result)
                {var focus=result.focus,focusTxt;if(focus==false)
                {focus='';focusTxt=_t('关注');}
                else
                {focus='active';focusTxt=_t('取消关注');}
                    $('#aw-ajax-box').html(Hogan.compile(AW_TEMPLATE.topicCard).render({'topic_id':result.topic_id,'topic_pic':result.topic_pic,'topic_title':result.topic_title,'topic_description':result.topic_description,'discuss_count':result.discuss_count,'focus_count':result.focus_count,'focus':focus,'focusTxt':focusTxt,'url':result.url,'fansCount':result.fans_count}));if(G_USER_ID=='')
                {$('#aw-card-tips .mod-footer .follow').hide();}
                    _init();AWS.G.cashTopicData.push($('#aw-ajax-box').html());},'json');}}
            function _checkcash(type)
            {if(type=='user')
            {$.each(AWS.G.cashUserData,function(i,a)
            {if(a.match('data-id="'+_this.attr('data-id')+'"'))
            {$('#aw-ajax-box').html(a);$('#aw-card-tips').removeAttr('style');_init();flag=1;}});}
                if(type=='topic')
                {$.each(AWS.G.cashTopicData,function(i,a)
                {if(a.match('data-id="'+_this.attr('data-id')+'"'))
                {$('#aw-ajax-box').html(a);$('#aw-card-tips').removeAttr('style');_init();flag=1;}});}}
            function _init()
            {var left=_this.offset().left,top=_this.offset().top+_this.height()+5,nTop=_this.offset().top-$(window).scrollTop();if(nTop+$('#aw-card-tips').innerHeight()>$(window).height())
            {top=_this.offset().top-($('#aw-card-tips').innerHeight())-10;}
                if(left+$('#aw-card-tips').innerWidth()>$(window).width())
                {left=_this.offset().left-$('#aw-card-tips').innerWidth()+_this.innerWidth();}
                $('#aw-card-tips').css({left:left,top:top}).fadeIn();}},time);});$(document).on('mouseout',selector,function()
    {clearTimeout(AWS.G.card_box_show_timer);AWS.G.card_box_hide_timer=setTimeout(function()
    {$('#aw-card-tips').fadeOut();},600);});},at_user_lists:function(selector,limit){$(selector).keyup(function(e){var _this=$(this),flag=_getCursorPosition($(this)[0]).start;if($(this).val().charAt(flag-1)=='@')
    {_init();$('#aw-ajax-box .content_cursor').html($(this).val().substring(0,flag));}else
    {var lis=$('.aw-invite-dropdown li');switch(e.which)
    {case 38:var _index;if(!lis.hasClass('active'))
        {lis.eq(lis.length-1).addClass('active');}
        else
        {$.each(lis,function(i,e)
        {if($(this).hasClass('active'))
        {$(this).removeClass('active');if($(this).index()==0)
        {_index=lis.length-1;}
        else
        {_index=$(this).index()-1;}}});lis.eq(_index).addClass('active');}
            break;case 40:var _index;if(!lis.hasClass('active'))
        {lis.eq(0).addClass('active');}
        else
        {$.each(lis,function(i,e)
        {if($(this).hasClass('active'))
        {$(this).removeClass('active');if($(this).index()==lis.length-1)
        {_index=0;}
        else
        {_index=$(this).index()+1;}}});lis.eq(_index).addClass('active');}
            break;case 13:$.each($('.aw-invite-dropdown li'),function(i,e)
        {if($(this).hasClass('active'))
        {$(this).click();}});break;default:if($('.aw-invite-dropdown')[0])
        {var ti=0;for(var i=flag;i>0;i--)
        {if($(this).val().charAt(i)=="@")
        {ti=i;break;}}
            $.get(G_BASE_URL+'/search/ajax/search/?type=users&q='+encodeURIComponent($(this).val().substring(flag,ti).replace('@',''))+'&limit='+limit,function(result)
            {if($('.aw-invite-dropdown')[0])
            {if(result.length!=0)
            {var html='';$('.aw-invite-dropdown').html('');$.each(result,function(i,a)
            {html+='<li><img src="'+a.detail.avatar_file+'"/><a>'+a.name+'</a></li>'});$('.aw-invite-dropdown').append(html);_display();$('.aw-invite-dropdown li').click(function()
            {_this.val(_this.val().substring(0,ti)+'@'+$(this).find('a').html()+" ").focus();$('.aw-invite-dropdown').detach();});}
            else
            {$('.aw-invite-dropdown').hide();}}
                if(_this.val().length==0)
                {$('.aw-invite-dropdown').hide();}},'json');}}}});$(selector).keydown(function(e){var key=e.which;if($('.aw-invite-dropdown').is(':visible')){if(key==38||key==40||key==13){return false;}}});function _init(){if(!$('.content_cursor')[0]){$('#aw-ajax-box').append('<span class="content_cursor"></span>');}
        $('#aw-ajax-box').find('.content_cursor').css({'left':parseInt($(selector).offset().left+parseInt($(selector).css('padding-left'))+2),'top':parseInt($(selector).offset().top+parseInt($(selector).css('padding-left')))});if(!$('.aw-invite-dropdown')[0])
        {$('#aw-ajax-box').append('<ul class="aw-invite-dropdown"></ul>');}};function _display(){$('.aw-invite-dropdown').css({'left':$('.content_cursor').offset().left+$('.content_cursor').innerWidth(),'top':$('.content_cursor').offset().top+24}).show();};function _getCursorPosition(textarea)
    {var rangeData={text:"",start:0,end:0};textarea.focus();if(textarea.setSelectionRange){rangeData.start=textarea.selectionStart;rangeData.end=textarea.selectionEnd;rangeData.text=(rangeData.start!=rangeData.end)?textarea.value.substring(rangeData.start,rangeData.end):"";}else if(document.selection){var i,oS=document.selection.createRange(),oR=document.body.createTextRange();oR.moveToElementText(textarea);rangeData.text=oS.text;rangeData.bookmark=oS.getBookmark();for(i=0;oR.compareEndPoints('StartToStart',oS)<0&&oS.moveStart("character",-1)!==0;i++){if(textarea.value.charAt(i)=='\n'){i++;}}
        rangeData.start=i;rangeData.end=rangeData.text.length+rangeData.start;}
        return rangeData;};},shake:function(selector)
    {var length=6;selector.css('position','relative');for(var i=1;i<=length;i++)
    {if(i%2==0)
    {if(i==length)
    {selector.animate({'left':0},50);}
    else
    {selector.animate({'left':10},50);}}
    else
    {selector.animate({'left':-10},50);}}}}
AWS.G={cashUserData:[],cashTopicData:[],card_box_hide_timer:'',card_box_show_timer:'',dropdown_list_xhr:'',loading_timer:'',loading_bg_count:12,loading_mini_bg_count:9,notification_timer:''}
AWS.User={follow:function(selector,type,data_id,status)
    {if(selector.html())
    {if(status){if(selector.hasClass('active'))
    {selector.find('span').html(_t('关注'));}
    else
    {selector.find('span').html(_t('取消关注'));}}else
    {if(selector.hasClass('active'))
    {selector.find('span').html(_t('关注'));selector.find('b').html(parseInt(selector.find('b').html())-1);}
    else
    {selector.find('span').html(_t('取消关注'));selector.find('b').html(parseInt(selector.find('b').html())+1);}}}
    else
    {if(selector.hasClass('active'))
    {selector.attr('data-original-title',_t('关注'));}
    else
    {selector.attr('data-original-title',_t('取消关注'));}}
        selector.addClass('disabled');switch(type)
    {case'question':var url='/question/ajax/focus/';var data={'question_id':data_id};break;case'topic':var url='/topic/ajax/focus_topic/';var data={'topic_id':data_id};break;case'user':var url='/follow/ajax/follow_people/';var data={'uid':data_id};break;case'column':var url='/column/ajax/focus_column/';var data={'column_id':data_id};break;}
        $.post(G_BASE_URL+url,data,function(result)
        {if(result.errno==1)
        {if(result.rsm.type=='add')
        {selector.addClass('active');}
        else
        {selector.removeClass('active');}}
        else
        {if(result.err)
        {AWS.alert(result.err);}
            if(result.rsm.url)
            {window.location=decodeURIComponent(result.rsm.url);}}
            selector.removeClass('disabled');},'json');},share_out:function(options)
    {var url=options.url||window.location.href,pic='';if(options.title)
    {var title=options.title+' - '+G_SITE_NAME;}
    else
    {var title=$('title').text();}
        shareURL='http://www.jiathis.com/send/?webid='+options.webid+'&url='+url+'&title='+title+'';if(options.content)
    {if($(options.content).find('img').length)
    {shareURL=shareURL+'&pic='+$(options.content).find('img').eq(0).attr('src');}}
        window.open(shareURL);},delete_draft:function(item_id,type)
    {if(type=='clean')
    {$.post(G_BASE_URL+'/account/ajax/delete_draft/','type='+type,function(result)
    {if(result.errno!=1)
    {AWS.alert(result.err);}},'json');}
    else
    {$.post(G_BASE_URL+'/account/ajax/delete_draft/','item_id='+item_id+'&type='+type,function(result)
    {if(result.errno!=1)
    {AWS.alert(result.err);}},'json');}},agree_vote:function(selector,user_name,answer_id)
    {$.post(G_BASE_URL+'/question/ajax/answer_vote/','answer_id='+answer_id+'&value=1');if($(selector).parents('.aw-item').find('.aw-agree-by').text().match(user_name))
    {$.each($(selector).parents('.aw-item').find('.aw-user-name'),function(i,e)
    {if($(e).html()==user_name)
    {if($(e).prev())
    {$(e).prev().remove();}
    else
    {$(e).next().remove();}
        $(e).remove();}});$(selector).removeClass('active');if(parseInt($(selector).parents('.operate').find('.count').html())!=0)
    {$(selector).parents('.operate').find('.count').html(parseInt($(selector).parents('.operate').find('.count').html())-1);}
        if($(selector).parents('.aw-item').find('.aw-agree-by a').length==0)
        {$(selector).parents('.aw-item').find('.aw-agree-by').hide();}}
    else
    {if($(selector).parents('.aw-item').find('.aw-agree-by .aw-user-name').length==0)
    {$(selector).parents('.aw-item').find('.aw-agree-by').append('<a class="aw-user-name">'+user_name+'</a>');}
    else
    {$(selector).parents('.aw-item').find('.aw-agree-by').append('<em>、</em><a class="aw-user-name">'+user_name+'</a>');}
        $(selector).parents('.operate').find('.count').html(parseInt($(selector).parents('.operate').find('.count').html())+1);$(selector).parents('.aw-item').find('.aw-agree-by').show();$(selector).parents('.operate').find('a.active').removeClass('active');$(selector).addClass('active');}},disagree_vote:function(selector,user_name,answer_id)
    {$.post(G_BASE_URL+'/question/ajax/answer_vote/','answer_id='+answer_id+'&value=-1',function(result){});if($(selector).hasClass('active'))
    {$(selector).removeClass('active');}
    else
    {if($(selector).parents('.operate').find('.agree').hasClass('active'))
    {$.each($(selector).parents('.aw-item').find('.aw-user-name'),function(i,e)
    {if($(e).html()==user_name)
    {if($(e).prev())
    {$(e).prev().remove();}
    else
    {$(e).next().remove();}
        $(e).remove();}});if($(selector).parents('.aw-item').find('.aw-agree-by a').length==0)
    {$(selector).parents('.aw-item').find('.aw-agree-by').hide();}
        $(selector).parents('.operate').find('.count').html(parseInt($(selector).parents('.operate').find('.count').html())-1);$(selector).parents('.operate').find('.agree').removeClass('active');$(selector).addClass('active');}
    else
    {$(selector).addClass('active');}}},question_uninterested:function(selector,question_id)
    {selector.fadeOut();$.post(G_BASE_URL+'/question/ajax/uninterested/','question_id='+question_id,function(result)
    {if(result.errno!='1')
    {AWS.alert(result.err);}},'json');},answer_force_fold:function(selector,answer_id)
    {$.post(G_BASE_URL+'/question/ajax/answer_force_fold/','answer_id='+answer_id,function(result){if(result.errno!=1)
    {AWS.alert(result.err);}
    else if(result.errno==1)
    {if(result.rsm.action=='fold')
    {selector.html(selector.html().replace(_t('折叠'),_t('撤消折叠')));}
    else
    {selector.html(selector.html().replace(_t('撤消折叠'),_t('折叠')));}}},'json');},question_invite_delete:function(selector,question_invite_id)
    {$.post(G_BASE_URL+'/question/ajax/question_invite_delete/','question_invite_id='+question_invite_id,function(result)
    {if(result.errno==1)
    {selector.fadeOut();}
    else
    {AWS.alert(result.rsm.err);}},'json');},invite_user:function(selector,img)
    {$.post(G_BASE_URL+'/question/ajax/save_invite/',{'question_id':QUESTION_ID,'uid':selector.attr('data-id')},function(result)
    {if(result.errno!=-1)
    {if(selector.parents('.aw-invite-box').find('.invite-list a').length==0)
    {selector.parents('.aw-invite-box').find('.invite-list').show();}
        selector.parents('.aw-invite-box').find('.invite-list').append(' <a class="text-color-999 invite-list-user" data-toggle="tooltip" data-placement="bottom" data-original-title="'+selector.attr('data-value')+'"><img src='+img+' /></a>');selector.addClass('active').attr('onclick','AWS.User.disinvite_user($(this))').text('取消邀请');selector.parents('.aw-question-detail').find('.aw-invite-replay .badge').text(parseInt(selector.parents('.aw-question-detail').find('.aw-invite-replay .badge').text())+1);}
    else if(result.errno==-1)
    {AWS.alert(result.err);}},'json');},disinvite_user:function(selector)
    {$.get(G_BASE_URL+'/question/ajax/cancel_question_invite/question_id-'+QUESTION_ID+"__recipients_uid-"+selector.attr('data-id'),function(result)
    {if(result.errno!=-1)
    {$.each($('.aw-question-detail .invite-list a'),function(i,e)
    {if($(this).attr('data-original-title')==selector.parents('.main').find('.aw-user-name').text())
    {$(this).detach();}});selector.removeClass('active').attr('onclick','AWS.User.invite_user($(this),$(this).parents(\'li\').find(\'img\').attr(\'src\'))').text('邀请');selector.parents('.aw-question-detail').find('.aw-invite-replay .badge').text(parseInt(selector.parents('.aw-question-detail').find('.aw-invite-replay .badge').text())-1);if(selector.parents('.aw-invite-box').find('.invite-list').children().length==0)
    {selector.parents('.aw-invite-box').find('.invite-list').hide();}}});},question_thanks:function(selector,question_id)
    {$.post(G_BASE_URL+'/question/ajax/question_thanks/','question_id='+question_id,function(result)
    {if(result.errno!=1)
    {AWS.alert(result.err);}
    else if(result.rsm.action=='add')
    {selector.html(selector.html().replace(_t('感谢'),_t('已感谢')));selector.removeAttr('onclick');}
    else
    {selector.html(selector.html().replace(_t('已感谢'),_t('感谢')));}},'json');},answer_user_rate:function(selector,type,answer_id)
    {$.post(G_BASE_URL+'/question/ajax/question_answer_rate/','type='+type+'&answer_id='+answer_id,function(result)
    {if(result.errno!=1)
    {AWS.alert(result.err);}
    else if(result.errno==1)
    {switch(type)
    {case'thanks':if(result.rsm.action=='add')
        {selector.html(selector.html().replace(_t('感谢'),_t('已感谢')));selector.removeAttr('onclick');}
        else
        {selector.html(selector.html().replace(_t('已感谢'),_t('感谢')));}
            break;case'uninterested':if(result.rsm.action=='add')
        {selector.html(selector.html().replace(_t('没有帮助'),_t('撤消没有帮助')));}
        else
        {selector.html(selector.html().replace(_t('撤消没有帮助'),_t('没有帮助')));}
            break;}}},'json');},save_comment:function(selector)
    {selector.addClass('disabled');AWS.ajax_post(selector.parents('form'),AWS.ajax_processer,'comments_form');},remove_comment:function(selector,type,comment_id)
    {$.get(G_BASE_URL+'/question/ajax/remove_comment/type-'+type+'__comment_id-'+comment_id);selector.parents('.aw-comment-box li').fadeOut();},article_vote:function(selector,article_id,rating)
    {AWS.loading('show');if(selector.hasClass('active'))
    {var rating=0;}
        $.post(G_BASE_URL+'/article/ajax/article_vote/','type=article&item_id='+article_id+'&rating='+rating,function(result){AWS.loading('hide');if(result.errno!=1)
        {AWS.alert(result.err);}
        else
        {if(rating==0)
        {selector.removeClass('active').find('b').html(parseInt(selector.find('b').html())-1);}
        else if(rating==-1)
        {if(selector.parents('.aw-article-vote').find('.agree').hasClass('active'))
        {selector.parents('.aw-article-vote').find('b').html(parseInt(selector.parents('.aw-article-vote').find('b').html())-1);selector.parents('.aw-article-vote').find('a').removeClass('active');}
            selector.addClass('active');}
        else
        {selector.parents('.aw-article-vote').find('a').removeClass('active');selector.addClass('active').find('b').html(parseInt(selector.find('b').html())+1);}}},'json');},article_comment_vote:function(selector,comment_id,rating)
    {AWS.loading('show');if(selector.hasClass('active'))
    {var rating=0;}
        $.post(G_BASE_URL+'/article/ajax/article_vote/','type=comment&item_id='+comment_id+'&rating='+rating,function(result)
        {AWS.loading('hide');if(result.errno!=1)
        {AWS.alert(result.err);}
        else
        {var agree_num=parseInt(selector.html().replace(/[^0-9]/ig,""));if(rating==0)
        {var selectorhtml=selector.html().replace(_t('我已赞'),_t('赞'));selector.html(selectorhtml.replace(agree_num,(agree_num-1))).removeClass('active');}
        else
        {var selectorhtml=selector.html().replace(_t('赞'),_t('我已赞'));selector.html(selectorhtml.replace(agree_num,(agree_num+1))).addClass('active');}}},'json');},add_favorite_tag:function()
    {$.post(G_BASE_URL+'/favorite/ajax/update_favorite_tag/',{'item_id':$('#favorite_form input[name="item_id"]').val(),'item_type':$('#favorite_form input[name="item_type"]').val(),'tags':$('#favorite_form .add-input').val()},function(result)
    {if(result.errno==1)
    {$('.aw-favorite-box .aw-favorite-tag-list').show();$('.aw-favorite-box .aw-favorite-tag-add').hide();$('.aw-favorite-tag-list ul').prepend('<li class="active"><a data-value="'+$('#favorite_form .add-input').val()+'"><span class="title">'+$('#favorite_form .add-input').val()+'</span></a><i class="icon icon-followed"></i></li>');}},'json');}}
AWS.Dropdown={bind_dropdown_list:function(selector,type)
    {if(type=='search')
    {$(selector).focus(function()
    {$(selector).parent().find('.aw-dropdown').show();});}
        $(selector).bind('input propertychange',function(e)
        {if(type=='search')
        {$(selector).parent().find('.search').show().children('a').text($(selector).val());}
            if($(selector).val().length>=1)
            {if(e.which!=38&&e.which!=40&&e.which!=188&&e.which!=13)
            {AWS.Dropdown.get_dropdown_list($(this),type,$(selector).val());}}
            else
            {$(selector).parent().find('.aw-dropdown').hide();}
            if(type=='topic')
            {if(e.which==188)
            {if($('.aw-edit-topic-box #aw_edit_topic_title').val()!=',')
            {$('.aw-edit-topic-box #aw_edit_topic_title').val($('.aw-edit-topic-box #aw_edit_topic_title').val().substring(0,$('.aw-edit-topic-box #aw_edit_topic_title').val().length-1));$('.aw-edit-topic-box .aw-dropdown').hide();$('.aw-edit-topic-box .add').click();}
                return false;}
                if(e.which==13)
                {$('.aw-edit-topic-box .aw-dropdown').hide();$('.aw-edit-topic-box .add').click();return false;}
                var lis=$(selector).parent().find('.aw-dropdown-list li');if(e.which==40&&lis.is(':visible'))
            {var _index;if(!lis.hasClass('active'))
            {lis.eq(0).addClass('active');}
            else
            {$.each(lis,function(i,e)
            {if($(this).hasClass('active'))
            {$(this).removeClass('active');if($(this).index()==lis.length-1)
            {_index=0;}
            else
            {_index=$(this).index()+1;}}});lis.eq(_index).addClass('active');$(selector).val(lis.eq(_index).text());}}
                if(e.which==38&&lis.is(':visible'))
                {var _index;if(!lis.hasClass('active'))
                {lis.eq(lis.length-1).addClass('active');}
                else
                {$.each(lis,function(i,e)
                {if($(this).hasClass('active'))
                {$(this).removeClass('active');if($(this).index()==0)
                {_index=lis.length-1;}
                else
                {_index=$(this).index()-1;}}});lis.eq(_index).addClass('active');$(selector).val(lis.eq(_index).text());}}}});$(selector).blur(function()
    {$(selector).parent().find('.aw-dropdown').delay(500).fadeOut(300);});},set_dropdown_list:function(selector,data,selected)
    {$(selector).append(Hogan.compile(AW_TEMPLATE.dropdownList).render({'items':data}));$(selector+' .aw-dropdown-list li a').click(function()
    {var text=$(this).text().length>6?$(this).text().substr(0,6)+'...':$(this).text()
        $(selector+' .dropdown-toggle span').html(text);});if(selected)
    {$(selector+" .dropdown-menu li a[data-value='"+selected+"']").click();}},get_dropdown_list:function(selector,type,data)
    {if(AWS.G.dropdown_list_xhr!='')
    {AWS.G.dropdown_list_xhr.abort();}
        var url;switch(type)
    {case'search':url=G_BASE_URL+'/search/ajax/search/?q='+encodeURIComponent(data)+'&limit=5';break;case'publish':url=G_BASE_URL+'/search/ajax/search/?type=questions&q='+encodeURIComponent(data)+'&limit=5';break;case'redirect':url=G_BASE_URL+'/search/ajax/search/?q='+encodeURIComponent(data)+'&type=questions&limit=30&is_question_id=1';break;case'invite':case'inbox':url=G_BASE_URL+'/search/ajax/search/?type=users&q='+encodeURIComponent(data)+'&limit=10';break;case'topic_question':url=G_BASE_URL+'/search/ajax/search/?type=questions,articles&q='+encodeURIComponent(data)+'&topic_ids='+CONTENTS_RELATED_TOPIC_IDS+'&limit=50';break;case'topic':url=G_BASE_URL+'/search/ajax/search/?type=topics&q='+encodeURIComponent(data)+'&limit=10';break;case'questions':url=G_BASE_URL+'/search/ajax/search/?type=questions&q='+encodeURIComponent(data)+'&limit=10';break;case'articles':url=G_BASE_URL+'/search/ajax/search/?type=articles&q='+encodeURIComponent(data)+'&limit=10';break;}
        AWS.G.dropdown_list_xhr=$.get(url,function(result)
        {if(result.length!=0&&AWS.G.dropdown_list_xhr!=undefined)
        {$(selector).parent().find('.aw-dropdown-list').html('');switch(type)
        {case'search':$.each(result,function(i,a)
            {switch(a.type)
            {case'questions':if(a.detail.best_answer>0)
                {var active='active';}
                else
                {var active=''}
                    $(selector).parent().find('.aw-dropdown-list').append(Hogan.compile(AW_TEMPLATE.searchDropdownListQuestions).render({'url':a.url,'active':active,'content':a.name,'discuss_count':a.detail.answer_count}));break;case'articles':$(selector).parent().find('.aw-dropdown-list').append(Hogan.compile(AW_TEMPLATE.searchDropdownListArticles).render({'url':a.url,'content':a.name,'comments':a.detail.comments}));break;case'topics':$(selector).parent().find('.aw-dropdown-list').append(Hogan.compile(AW_TEMPLATE.searchDropdownListTopics).render({'url':a.url,'name':a.name,'discuss_count':a.detail.discuss_count,'topic_id':a.detail.topic_id}));break;case'users':if(a.detail.signature=='')
                {var signature=_t('暂无介绍');}
                else
                {var signature=a.detail.signature;}
                    $(selector).parent().find('.aw-dropdown-list').append(Hogan.compile(AW_TEMPLATE.searchDropdownListUsers).render({'url':a.url,'img':a.detail.avatar_file,'name':a.name,'intro':signature}));break;}});break;case'publish':case'topic_question':$.each(result,function(i,a)
            {$(selector).parent().find('.aw-dropdown-list').append(Hogan.compile(AW_TEMPLATE.questionDropdownList).render({'url':a.url,'name':a.name}));});break;case'topic':$.each(result,function(i,a)
            {$(selector).parent().find('.aw-dropdown-list').append(Hogan.compile(AW_TEMPLATE.editTopicDorpdownList).render({'name':a['name']}));});break;case'redirect':$.each(result,function(i,a)
            {$(selector).parent().find('.aw-dropdown-list').append(Hogan.compile(AW_TEMPLATE.questionRedirectList).render({'url':"'"+G_BASE_URL+"/question/ajax/redirect/', 'item_id="+$(selector).attr('data-id')+"&target_id="+a['search_id']+"'",'name':a['name']}));});break;case'questions':case'articles':$.each(result,function(i,a)
            {$(selector).parent().find('.aw-dropdown-list').append(Hogan.compile(AW_TEMPLATE.questionDropdownList).render({'url':'#','name':a['name']}));});break;$(selector).parent().find('.aw-dropdown-list li').click(function()
            {$('.aw-question-list').append('<li data-id="'+$(this).attr('data-id')+'"><div class="col-sm-9">'+$(this).html()+'</div> <div class="col-sm-3"><a class="btn btn-danger btn-xs">删除</a></div></li>');$('.aw-question-list li').find("a").attr('href',function(){return $(this).attr("_href")});if($('.question_ids').val()=='')
            {$('.question_ids').val($(this).attr('data-id')+',');}
            else
            {$('.question_ids').val($('.question_ids').val()+$(this).attr('data-id')+',');}
                $(".alert-box").modal('hide');});break;case'inbox':case'invite':$.each(result,function(i,a)
            {$(selector).parent().find('.aw-dropdown-list').append(Hogan.compile(AW_TEMPLATE.inviteDropdownList).render({'uid':a.uid,'name':a.name,'img':a.detail.avatar_file}));});break;}
            if(type=='publish')
            {$(selector).parent().find('.aw-publish-suggest-question, .aw-publish-suggest-question .aw-dropdown-list').show();}
            else
            {$(selector).parent().find('.aw-dropdown, .aw-dropdown-list').show().children().show();$(selector).parent().find('.title').hide();$(selector).parent().find('.aw-dropdown-list li.question a').highText(data,'b','active');}}else
        {$(selector).parent().find('.aw-dropdown').show().end().find('.title').html(_t('没有找到相关结果')).show();$(selector).parent().find('.aw-dropdown-list, .aw-publish-suggest-question').hide();}},'json');}}
AWS.Message={check_notifications:function()
    {if(G_USER_ID==0)
    {clearInterval(AWS.G.notification_timer);return false;}
        $.get(G_BASE_URL+'/home/ajax/notifications/',function(result)
        {$('#inbox_unread').html(Number(result.rsm.inbox_num));var last_unread_notification=G_UNREAD_NOTIFICATION;G_UNREAD_NOTIFICATION=Number(result.rsm.notifications_num);if(G_UNREAD_NOTIFICATION>0)
        {if(G_UNREAD_NOTIFICATION!=last_unread_notification)
        {AWS.Message.load_notification_list();$('#notifications_unread').html(G_UNREAD_NOTIFICATION);}
            document.title='('+(Number(result.rsm.notifications_num)+Number(result.rsm.inbox_num))+') '+document_title;$('#notifications_unread').show();}
        else
        {if($('#header_notification_list').length)
        {$("#header_notification_list").html('<p class="aw-padding10" align="center">'+_t('没有未读通知')+'</p>');}
            if($("#index_notification").length)
            {$("#index_notification").fadeOut();}
            document.title=document_title;$('#notifications_unread').hide();}
            if(Number(result.rsm.inbox_num)>0)
            {$('#inbox_unread').show();}
            else
            {$('#inbox_unread').hide();}},'json');},read_notification:function(selector,notification_id,reload)
    {if(notification_id)
    {selector.remove();var url=G_BASE_URL+'/notifications/ajax/read_notification/notification_id-'+notification_id;}
    else
    {if($("#index_notification").length)
    {$("#index_notification").fadeOut();}
        var url=G_BASE_URL+'/notifications/ajax/read_notification/';}
        $.get(url,function(result)
        {AWS.Message.check_notifications();if(reload)
        {window.location.reload();}});},load_notification_list:function()
    {if($("#index_notification").length)
    {$("#index_notification").fadeIn().find('[name=notification_unread_num]').html(G_UNREAD_NOTIFICATION);$('#index_notification ul#notification_list').html('<p align="center" style="padding: 15px 0"><img src="'+G_STATIC_URL+'/common/loading_b.gif"/></p>');$.get(G_BASE_URL+'/notifications/ajax/list/flag-0__page-0',function(result)
    {$('#index_notification ul#notification_list').html(result);AWS.Message.notification_show(5);});}
        if($("#header_notification_list").length)
        {$.get(G_BASE_URL+'/notifications/ajax/list/flag-0__limit-5__template-header_list',function(result)
        {if(result.length)
        {$("#header_notification_list").html(result);}
        else
        {$("#header_notification_list").html('<p class="aw-padding10" align="center">'+_t('没有未读通知')+'</p>');}});}},notification_show:function(length)
    {if($('#index_notification').length>0)
    {if($('#index_notification ul#notification_list li').length==0)
    {$('#index_notification').fadeOut();}
    else
    {$('#index_notification ul#notification_list li').each(function(i,e)
    {if(i<length)
    {$(e).show();}
    else
    {$(e).hide();}});}}}}
AWS.Init={init_comment_box:function(selector)
    {$(document).on('click',selector,function()
    {$(this).parents('.aw-question-detail').find('.aw-invite-box, .aw-question-related-box').hide();if(typeof COMMENT_UNFOLD!='undefined')
    {if(COMMENT_UNFOLD=='all'&&$(this).attr('data-comment-count')==0&&$(this).attr('data-first-click')=='hide')
    {$(this).removeAttr('data-first-click');return false;}}
        if(!$(this).attr('data-type')||!$(this).attr('data-id'))
        {return true;}
        var comment_box_id='#aw-comment-box-'+$(this).attr('data-type')+'-'+　$(this).attr('data-id');if($(comment_box_id).length)
    {if($(comment_box_id).css('display')=='none')
    {$(this).addClass('active');$(comment_box_id).fadeIn();}
    else
    {$(this).removeClass('active');$(comment_box_id).fadeOut();}}
    else
    {switch($(this).attr('data-type'))
    {case'question':var comment_form_action=G_BASE_URL+'/question/ajax/save_question_comment/question_id-'+$(this).attr('data-id');var comment_data_url=G_BASE_URL+'/question/ajax/get_question_comments/question_id-'+$(this).attr('data-id');break;case'answer':var comment_form_action=G_BASE_URL+'/question/ajax/save_answer_comment/answer_id-'+$(this).attr('data-id');var comment_data_url=G_BASE_URL+'/question/ajax/get_answer_comments/answer_id-'+$(this).attr('data-id');break;}
        if(G_USER_ID)
        {$(this).parents('.aw-item').find('.mod-footer').append(Hogan.compile(AW_TEMPLATE.commentBox).render({'comment_form_id':comment_box_id.replace('#',''),'comment_form_action':comment_form_action}));$(comment_box_id).find('.aw-comment-txt').bind({focus:function()
            {$(comment_box_id).find('.aw-comment-box-btn').show();},blur:function()
            {$(".aw-invite-dropdown").hide();if($(this).val()=='')
            {$(comment_box_id).find('.aw-comment-box-btn').hide();}}});$(comment_box_id).find('.close-comment-box').click(function()
        {$(comment_box_id).fadeOut();$(comment_box_id).find('.aw-comment-txt').css('height',$(this).css('line-height'));});}
        else
        {$(this).parents('.aw-item').find('.mod-footer').append(Hogan.compile(AW_TEMPLATE.commentBoxClose).render({'comment_form_id':comment_box_id.replace('#',''),'comment_form_action':comment_form_action}));}
        $.get(comment_data_url,function(result)
        {if($.trim(result)=='')
        {result='<div align="center" class="aw-padding10">'+_t('暂无评论')+'</div>';}
            $(comment_box_id).find('.aw-comment-list').html(result);});$(comment_box_id).find('.aw-comment-txt').autosize();$(this).addClass('active');AWS.at_user_lists(comment_box_id+' .aw-comment-txt',5);}});},init_article_comment_box:function(selector)
    {$("#comment_editor").blur(function(){$(".aw-invite-dropdown").hide();});$(document).on('click',selector,function()
    {var _editor_box=$(this).parents('.aw-item').find('.aw-article-replay-box');if(_editor_box.length)
    {if(_editor_box.css('display')=='block')
    {_editor_box.fadeOut();}
    else
    {_editor_box.fadeIn();}}
    else
    {$(this).parents('.mod-footer').append(Hogan.compile(AW_TEMPLATE.articleCommentBox).render({'at_uid':$(this).attr('data-id'),'article_id':$('.aw-topic-bar').attr('data-id')}));}});},init_topic_edit_box:function(selector)
    {$(selector).click(function()
    {var _topic_editor=$(this).parents('.aw-topic-bar'),data_id=_topic_editor.attr('data-id'),data_type=_topic_editor.attr('data-type');if(!_topic_editor.hasClass('active'))
    {_topic_editor.addClass('active');if(!_topic_editor.find('.topic-tag .close').length)
    {_topic_editor.find('.topic-tag').append('<a class="close"><i class="icon icon-delete"></i></a>');}}
    else
    {_topic_editor.addClass('active');}
        if(_topic_editor.find('.aw-edit-topic-box').length==0)
        {_topic_editor.append(AW_TEMPLATE.editTopicBox);_topic_editor.find('.add').click(function()
        {if(_topic_editor.find('#aw_edit_topic_title').val()!='')
        {switch(data_type)
        {case'publish':var str=_topic_editor.find('#aw_edit_topic_title').val();if(str.indexOf("/")!=-1||str.indexOf("-")!=-1||str.indexOf("&")!=-1){AWS.alert('话题标题不能包含 / - &');return false;}
                _topic_editor.find('.tag-bar').prepend('<span class="topic-tag"><a class="text">'+_topic_editor.find('#aw_edit_topic_title').val()+'</a><a class="close" onclick="$(this).parents(\'.topic-tag\').remove();"><i class="icon icon-delete"></i></a><input type="hidden" value="'+_topic_editor.find('#aw_edit_topic_title').val()+'" name="topics[]" /></span>').hide().fadeIn();_topic_editor.find('#aw_edit_topic_title').val('');break;case'question':$.post(G_BASE_URL+'/topic/ajax/save_topic_relation/','type=question&item_id='+data_id+'&topic_title='+encodeURIComponent(_topic_editor.find('#aw_edit_topic_title').val()),function(result)
            {if(result.errno!=1)
            {AWS.alert(result.err);console.info(result.err);return false;}
                _topic_editor.find('.tag-bar').prepend('<span class="topic-tag" data-id="'+result.rsm.topic_id+'"><a href="'+G_BASE_URL+'/topic/'+result.rsm.topic_id+'" class="text">'+_topic_editor.find('#aw_edit_topic_title').val()+'</a><a class="close"><i class="icon icon-delete"></i></a></span>').hide().fadeIn();_topic_editor.find('#aw_edit_topic_title').val('');},'json');break;case'article':$.post(G_BASE_URL+'/topic/ajax/save_topic_relation/','type=article&item_id='+data_id+'&topic_title='+encodeURIComponent(_topic_editor.find('#aw_edit_topic_title').val()),function(result)
            {if(result.errno!=1)
            {AWS.alert(result.err);return false;}
                _topic_editor.find('.tag-bar').prepend('<span class="topic-tag" data-id="'+result.rsm.topic_id+'"><a href="'+G_BASE_URL+'/topic/'+result.rsm.topic_id+'" class="text">'+_topic_editor.find('#aw_edit_topic_title').val()+'</a><a class="close"><i class="icon icon-delete"></i></a></span>').hide().fadeIn();_topic_editor.find('#aw_edit_topic_title').val('');},'json');break;case'topic':$.post(G_BASE_URL+'/topic/ajax/save_related_topic/topic_id-'+data_id,'topic_title='+encodeURIComponent(_topic_editor.find('#aw_edit_topic_title').val()),function(result)
            {if(result.errno!=1)
            {AWS.alert(result.err);return false;}
                _topic_editor.find('.tag-bar').prepend('<span class="topic-tag"><a href="'+G_BASE_URL+'/favorite/tag-'+encodeURIComponent(_topic_editor.find('#aw_edit_topic_title').val())+'" class="text">'+_topic_editor.find('#aw_edit_topic_title').val()+'</a><a class="close"><i class="icon icon-delete"></i></a></span>').hide().fadeIn();_topic_editor.find('#aw_edit_topic_title').val('');},'json');break;case'favorite':$.post(G_BASE_URL+'/favorite/ajax/update_favorite_tag/','item_id='+data_id+'&item_type='+_topic_editor.attr('data-item-type')+'&tags='+encodeURIComponent(_topic_editor.find('#aw_edit_topic_title').val()),function(result)
            {if(result.errno!=1)
            {AWS.alert(result.err);return false;}
                _topic_editor.find('.tag-bar').prepend('<span class="topic-tag"><a href="'+G_BASE_URL+'/favorite/tag-'+encodeURIComponent(_topic_editor.find('#aw_edit_topic_title').val())+'" class="text">'+_topic_editor.find('#aw_edit_topic_title').val()+'</a><a class="close"><i class="icon icon-delete"></i></a></span>').hide().fadeIn();_topic_editor.find('#aw_edit_topic_title').val('');},'json');break;}}});_topic_editor.find('.close-edit').click(function()
        {_topic_editor.removeClass('active');_topic_editor.find('.aw-edit-topic-box').hide();_topic_editor.find('.aw-edit-topic').show();});AWS.Dropdown.bind_dropdown_list($(this).parents('.aw-topic-bar').find('#aw_edit_topic_title'),'topic');}
        $(this).parents('.aw-topic-bar').find('.aw-edit-topic-box').fadeIn();if(!G_CAN_CREATE_TOPIC)
    {$(this).parents('.aw-topic-bar').find('.add').hide();}
        $(this).hide();});}}
function _t(string,replace)
{if(typeof(aws_lang)!='undefined')
{if(typeof(aws_lang[string])!='undefined')
{string=aws_lang[string];}}
    if(replace)
    {string=string.replace('%s',replace);}
    return string;};(function($)
{$.fn.extend({insertAtCaret:function(textFeildValue)
    {var textObj=$(this).get(0);if(document.all&&textObj.createTextRange&&textObj.caretPos)
    {var caretPos=textObj.caretPos;caretPos.text=caretPos.text.charAt(caretPos.text.length-1)==''?textFeildValue+'':textFeildValue;}
    else if(textObj.setSelectionRange)
    {var rangeStart=textObj.selectionStart,rangeEnd=textObj.selectionEnd,tempStr1=textObj.value.substring(0,rangeStart),tempStr2=textObj.value.substring(rangeEnd);textObj.value=tempStr1+textFeildValue+tempStr2;textObj.focus();var len=textFeildValue.length;textObj.setSelectionRange(rangeStart+len,rangeStart+len);textObj.blur();}
    else
    {textObj.value+=textFeildValue;}},highText:function(searchWords,htmlTag,tagClass)
    {return this.each(function()
    {$(this).html(function high(replaced,search,htmlTag,tagClass)
    {var pattarn=search.replace(/\b(\w+)\b/g,"($1)").replace(/\s+/g,"|");return replaced.replace(new RegExp(pattarn,"ig"),function(keyword)
    {return $("<"+htmlTag+" class="+tagClass+">"+keyword+"</"+htmlTag+">").outerHTML();});}($(this).text(),searchWords,htmlTag,tagClass));});},outerHTML:function(s)
    {return(s)?this.before(s).remove():jQuery("<p>").append(this.eq(0).clone()).html();}});$.extend({scrollTo:function(type,duration,options)
    {if(typeof type=='object')
    {var type=$(type).offset().top}
        $('html, body').animate({scrollTop:type},{duration:duration,queue:options.queue});}})})(jQuery);