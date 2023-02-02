package cn.lliiooll.ppbuff.data

import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.utils.findId

object ZyLiteTypes {

    val postList = sortedMapOf<String, Int>().apply {
        put("HolderStoryTips", -100);
        put("直播 -> 颜值直播", 0);
        put("直播 -> 交友直播", 4);
        put("话题推荐帖子", 16);
        put("直播推荐帖子", 211);
        put("直播卡片", 213);
        put("带游戏卡片的视频帖子", 216);
        put("语音卡片帖子", 212);
        put("语音房卡片帖子", 214);
        put("PostCurrencyHolderCard", 215);
        put("话题卡片帖子", 17);
        put("HotEventActivityCard", 299);
        put("皮皮村帖子", -101);
        put("语音帖子/评论", 2);
        put("混合广告帖子", 27);
        put("FeedActivityHolder", 105);
        put("视频", 11);
        put("普通视频帖子带游戏广告", 0x3c);
        put("单一图片帖子", 12);
        put("GIF帖子", 13);
        put("GIF&视频混合帖子", 14);
        put("PostViewHolderNormal", 101);
        put("PartitionHeadHolder", -10);
        put("HolderStoryAlert", -102);
        put("BottomInfoHolder", 101);
        put("普通图片/文字帖", 1);
        put("推荐关注帖子", 217);
        put("未知直播帖子类型", 200);
        put("HolderPrivacyAlert", "layout_holder_privacy_alert".findId("layout"));

        put("网页帖子", 102)
    }


    val mineList = sortedMapOf<String, String>().apply {
        put("功能入口> 游戏中心> 提醒", "my_tab_game_center_warn");
        put("功能入口> 吹水日记> 提醒", "my_tab_tree_new_bee_warn");
        put("功能入口> 设置> 提醒", "my_tab_setting_warn");
        put("功能入口> 免广告> 提醒", "avoid_ad_warn");
        put("功能入口> 共建家园", "my_tab_star_review");
        put("功能入口> 皮皮公益", "my_tab_public_welfare");
        put("功能入口> 我的背包", "my_tab_prize_package");
        put("功能入口> 皮皮短剧", "my_tab_skit_layout");
        put("功能入口> 免广告", "avoid_ad");
        put("功能入口> 皮皮直播", "my_tab_live");
        put("功能入口> 游戏中心", "my_tab_game_center_layout");
        put("轮播台", "bannerView");
        put("功能入口> 每日抽奖", "my_tab_lottery_layout");
        put("功能入口> 帮助反馈", "my_tab_help");
        put("功能入口> 官方认证", "my_tab_apply_kol");
        put("功能入口> 小黑屋", "my_tab_black_layout");
        put("功能入口> 吹水日记", "my_tab_tree_new_bee");
        put("功能入口> 锦旗墙", "my_tab_pennants_layout");
        put("功能入口> 设置", "my_tab_setting_layout");
        put("个人信息> 全部", "headerView");
        put("个人数据> 全部", "myTabDataLayout");
        put("个人数据> 发帖", "!my_data_post");
        put("个人数据> 评论", "!my_data_comment");
        put("个人数据> 点赞", "!my_data_like");
        put("个人数据> 收藏", "!my_data_collect");
        put("个人数据> 浏览历史", "!my_data_history");
        put("个人数据> 插眼", "!my_data_mark_eye");
        put("个人数据> 下载", "!my_data_download");
    }
    val extraMineList = sortedMapOf<String, String>().apply {
        put("my_tab_tree_new_bee","treeNewBeeLayout")
        put("my_tab_black_layout","blackLayout")
        put("my_tab_lottery_layout","lotteryLayout")
        put("my_tab_game_center_layout","gameCenterLayout")
        put("my_tab_live","liveLayout")
    }

}

fun Int.isHidePost(): Boolean {
    return PConfig.isHidePost(this)
}

fun Int.hidePost() {
    if (!isHidePost())
        PConfig.addHidePost(this)
    else
        PConfig.delHidePost(this)
}

fun String.isHideMine(): Boolean {
    return PConfig.isHideMine(this)
}

fun String.hideMine() {
    if (!isHideMine())
        PConfig.addHideMine(this)
    else
        PConfig.delHideMine(this)
}