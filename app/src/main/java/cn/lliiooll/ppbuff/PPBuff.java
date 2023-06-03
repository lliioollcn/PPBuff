package cn.lliiooll.ppbuff;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import cn.lliiooll.ppbuff.app.PPBuffApp;
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteDebugHook;
import cn.lliiooll.ppbuff.tracker.PLog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PPBuff {
    private static String modulePath;
    private static Map<String, String> hostAppClassNames = new HashMap<String, String>() {{
        put(HostInfo.ZuiyouLite.PACKAGE_NAME, "cn.xiaochuankeji.zuiyouLite.app.AppController");// 皮皮搞笑
        put(HostInfo.TieBa.PACKAGE_NAME, "cn.xiaochuankeji.tieba.AppController");// 最右
    }};
    private static Map<String, String> hostAppSettingClassNames = new HashMap<String, String>() {{
        put(HostInfo.ZuiyouLite.PACKAGE_NAME, "cn.xiaochuankeji.zuiyouLite.ui.setting.SettingActivity");// 皮皮搞笑
        put(HostInfo.TieBa.PACKAGE_NAME, "cn.xiaochuankeji.tieba.ui.home.setting.SettingActivity");// 最右
    }};
    private static Application app;
    public static boolean checked = false;
    public static boolean hasUpdate = false;
    public static boolean isFollow = false;
    public static boolean lspatch = false;


    public static boolean isDebug() {

        return BuildConfig.DEBUG || ZuiYouLiteDebugHook.INSTANCE.isEnable();
        //return false;
    }


    public static void initModulePath(String modulePath) {
        PPBuff.modulePath = modulePath;
    }

    public static String getModulePath() {
        return modulePath;
    }


    public static String getHostApplicationClassName(String packageName) {
        return hostAppClassNames.getOrDefault(packageName, "");
    }

    public static boolean isSupportApp(String packageName) {
        return hostAppClassNames.containsKey(packageName);
    }


    public static String getModulePackName() {
        return "cn.lliiooll.ppbuff";
    }


    public static String getHostApplicationSettingClassName(@NotNull String packageName) {
        return hostAppSettingClassNames.getOrDefault(packageName, "");
    }

    public static void init(@NotNull Application app) {
        PPBuff.app = app;
    }

    public static Application getApplication() {
        return app;
    }


    public static int getHostVersionCode() {
        Application app = getApplication();
        try {
            if (app != null) {
                PackageManager pm = app.getPackageManager();
                PackageInfo info = pm.getPackageInfo(app.getPackageName(), 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    return (int) info.getLongVersionCode();
                } else {
                    return info.versionCode;
                }
            }
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
        return 0;
    }

    public static String getHostVersionName() {
        Application app = getApplication();
        try {
            if (app != null) {
                PackageManager pm = app.getPackageManager();
                PackageInfo info = pm.getPackageInfo(app.getPackageName(), 0);
                return info.versionName;
            }
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
        return "null";
    }

    public static String getHostPath() {
        return getApplication().getClassLoader().getResource("AndroidManifest.xml").getPath().replace("!/AndroidManifest.xml", "").replaceFirst("file:", "");
    }

    public static String doReplace(String clazz) {
        String cl = clazz;
        if (cl.startsWith("L")) {
            cl = cl.replaceFirst("L", "");
        }
        if (cl.endsWith(";")) {
            cl = cl.substring(0, cl.length() - 1);
        }
        if (cl.contains("\\")) {
            cl = cl.replace("\\", ".");
        }
        if (cl.contains("/")) {
            cl = cl.replace("/", ".");
        }
        return cl;
    }

    public static String getAbiForLibrary() {
        String[] supported = Process.is64Bit() ? Build.SUPPORTED_64_BIT_ABIS : Build.SUPPORTED_32_BIT_ABIS;
        if (supported == null || supported.length == 0) {
            throw new IllegalStateException("No supported ABI in this device");
        }
        List<String> abis = Arrays.asList("armeabi-v7a", "arm64-v8a");
        for (String abi : supported) {
            if (abis.contains(abi)) {
                return abi;
            }
        }
        throw new IllegalStateException("No supported ABI in " + Arrays.toString(supported));
    }

    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        //获取状态栏高度的资源id
        if (ctx == null) {
            return result;
        }
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }


        return result;
    }

    public static boolean isInHostApp() {
        return app != null && app.getClass() != PPBuffApp.class;
    }

    @Nullable
    public static CharSequence loadEula() {
        return "请务必仔细阅读和理解通用许可协议书中规定的所有权利和限制。在使用前，您需要仔细阅读并决定接受或不接受本协议的条款。除非或直至您接受本协议的条款，否则本作品及其相关副本、相关程序代码或相关资源不得在您的任何终端上下载、安装或使用。\n" +
                "\n" +
                "您一旦下载、使用本作品及其相关副本、相关程序代码或相关资源，即表示您同意接受本协议各项条款的约束。如您不同意本协议中的条款，您则应当立即删除本作品、附属资源及其相关源代码。\n" +
                "\n" +
                "本作品权利只许可使用，而不出售。\n" +
                "\n" +
                "1. 定义\n" +
                "\n" +
                "   1. “本协议”指通用许可协议第二版。\n" +
                "\n" +
                "   2. “您”指依据本通用许可协议行使其所获得授予之权利的个人或机构。 “您的” 有相应的含义。\n" +
                "\n" +
                "   3. “作者”或“本作品作者”指通过本协议进行授权的个人或组织和/或根据《与贸易有关的知识产权协定》所规定的对本作品拥有著作权的个人或组织。\n" +
                "\n" +
                "   4. “协议作者”指上文提及的协议版权方，即 James Clef <qwq233@qwq2333.top, qwq2333.top>。\n" +
                "\n" +
                "   5. “本作品”或“作品”指根据本协议的任何受版权保护的作品，如源代码。\n" +
                "\n" +
                "   6. “本作品发布网址”指本作品作者初次或后续发布的所指定的唯一或多个的网址。\n" +
                "\n" +
                "   7. “修改”作品是指以需要版权许可的方式复制或改编该作品的全部或部分内容，而不是制作一个完全的副本。由此产生的作品也被称为本作品的\"修改版\"或“基于”本作品的作品。\n" +
                "\n" +
                "   8. “修改作品作者”指任何依据本通用许可协议对在修改作品中所贡献的部分所享有的著作权的个人或机构。\n" +
                "\n" +
                "   9. “传播”作品指对该作品进行任何未经许可的行为，这将使您直接或次要根据适用的版权法承担侵权责任，但在计算机上执行该作品或修改其私人副本除外。传播包括复制、分发（进行或不进行修改）、向公众公开、以及在某些国家/地区进行其他活动。\n" +
                "\n" +
                "   10. “非商业使用”指该使用的主要意图或者指向并非获取商业优势或金钱报酬。为本协议之目的，以数字文件共享或类似方式，用本作品交换其他受到著作权与类似权利保护的作品是非商业性使用，只要该交换不直接或潜在涉及金钱报酬的支付。\n" +
                "\n" +
                "   11. “商业使用”指该使用的主要意图或者指向为获取商业优势或金钱报酬。以数字文件共享或类似方式，用本作品交换其他受到著作权与类似权利保护的作品是商业性使用，只要该交换直接或潜在涉及金钱报酬的支付。\n" +
                "\n" +
                "   12. “源代码”指生成、安装和（对于可执行作品）运行目标代码以及修改作品所需的所有源代码，包括控制这些活动的脚本。但是，它不包括作品的系统库，也不包括在执行这些活动时未经修改但不属于作品的通用工具或普遍可用的免费程序。\n" +
                "\n" +
                "   13. “目标代码”指通过本作品源代码或修改作品源代码生成的计算机可识别的机器语言或近似与机器语言的代码。“编译作品”有相同含义。\n" +
                "\n" +
                "\n" +
                "2. 本协议无意削减、限制、或约束您基于以下法律规定对本作品的合法使用：合理使用，权利穷竭原则，及著作权法或其他相关法律对著作权人专有权利的限制。\n" +
                "\n" +
                "3. 授权范围\n" +
                "\n" +
                "   根据本协议的条款和条件，作者在此授予您全球性、免版税、非独占并且在本作品的著作权存续期间内均有效的许可，就本作品行使以下权利：\n" +
                "\n" +
                "   1. 在一台个人所有终端上安装、使用、显示、运行本作品的一份副本。\n" +
                "\n" +
                "   2. 为了防止副本损坏而制作备份复制品。这些备份复制品不得通过任何方式提供给他人使用，并在您丧失该合法副本的所有权时，负责将备份复制品销毁。\n" +
                "\n" +
                "   3. 为了把本作品用于实际的终端应用环境或者改进其功能、性能而进行必要的修改。\n" +
                "\n" +
                "   4. 对本作品进行反向工程、反向编译或反汇编；或进行其他获得本作品源代码的访问或行为。\n" +
                "\n" +
                "   5. 发行、公开传播本作品及其修改作品。\n" +
                "\n" +
                "   6. 根据本协议的条款，作者授予您在全球范围内，免费的、不可再许可、非独占、不可撤销的许可，以对本作品行使以下“协议所授予的权利”：\n" +
                "\n" +
                "      1. 复制和分享本作品的全部或部分，仅限于非商业性使用。\n" +
                "\n" +
                "      2. 以非商业使用为目的制作、复制和分享修改作品。\n" +
                "\n" +
                "   以上权利可在任何现有的或者以后出现的并为可适用的法律认可的媒体和形式上行使。上述权利包括为在其他媒体和形式上行使权利而必须进行技术性修改的权利。作者在此保留所有未明示授予的权利。\n" +
                "\n" +
                "4. 限制\n" +
                "\n" +
                "   1. 您在发行或公开传播本作品时，必须遵守本协议。在您发行或公开传播的本作品的每一份复制件中，您必须附上一份本协议协议的复制件。您不得就本作品提出或增加任何条款，从而限制本协议协议或者限制获得本作品的第三方行使本协议协议所赋予的权利。您不得对本作品进行再许可。您必须在您发行或公开传播的每份作品复制件中完整保留所有与本协议协议及免责条款相关的声明。 在发行或公开传播本作品时，您不得对本作品施加任何技术措施，从而限制从您处获得本作品的第三方行使本协议协议授予的权利。\n" +
                "\n" +
                "   2. 您必须以下述许可条款发行或公开传播修改作品：\n" +
                "\n" +
                "        1. 本协议或后续版本 \n" +
                "\n" +
                "        2. 您不得就修改作品提出或增加任何条款，从而限制“可适用的协议”的规定，或者限制获得修改作品的第三方行使“可适用的协议”所赋予的权利。在发行或公开传播包含本作品的修改作品时，您必须在本作品的每一份复制件中完整地保留所有与“可适用的协议”及免责条款相关的声明。在发行或公开传播修改作品时，您不得对修改作品施加任何技术措施，从而限制从您处获得修改作品的第三方行使“可适用的协议”所赋予的权利。本项（第 4 款第 2 项）规定同样适用于收录在汇编作品中的修改作品，但并不要求汇编作品中除基于本作品而创作的修改作品之外的其他作品受“可适用的协议”的约束。\n" +
                "\n" +
                "        3. 以源代码的形式传播本作品或编译作品时，您必须满足以下所有条件:\n" +
                "\n" +
                "            1. 修改作品必须有醒目的声明，说明您修改了它，并给出相关的日期。\n" +
                "\n" +
                "            2. 若修改作品使用了本作品所包含全部或部分源代码和/或其他部分的本作品，需提供完整的修改作品，如其全部源代码、可用于生成修改作品的编译作品的脚本、修改作品使用到的资源。\n" +
                "\n" +
                "        4. 以编译作品的形式传播本作品或修改作品时，以下列方式之一传递源代码:\n" +
                "\n" +
                "            1. 在实体产品（包括实体销售媒介）中传递编译作品，或体现在实体产品（包括实体销售媒介）中，同时将相应的源代码固定在通常用于软件交换的实体媒介上。\n" +
                "\n" +
                "            2. 点对点传输。\n" +
                "\n" +
                "            3. 可免费访问的网络服务器。\n" +
                "\n" +
                "   3. 您不得进行商业使用，这里的商业使用包括第 1 款第 10 项所提到的内容、以盈利为目的的提供本作品和/或修改作品的帮助和/或指南、以盈利为目的的使用或授予他人本作品的所提供给您的权利/许可。\n" +
                "\n" +
                "   4. 在发行或公开传播本作品、任何修改作品时，您必须完整保留所有关于本作品的著作权声明，并以适于所使用的媒介或方法的形式提供下述信息：\n" +
                "\n" +
                "        1. 作者的姓名或者其他能够体现作者身份的标志物。\n" +
                "\n" +
                "        2. 标明本作品发布网址\n" +
                "\n" +
                "        3. 依第 4 条第 2 项之要求，注明修改作品中使用的本作品的作者的姓名或者其他能够体现作者身份的标志物和作品名称。为避免疑义，本条有关标示作者姓名和作品名称之规定，仅适用于前述署名的用途；除非您事先另行取得作者的书面同意，否则您不得以明示或者默示的方式主张或暗示，您本人或您对作品的使用与作者有关联或者已获得上述人士的赞助或者支持。\n" +
                "\n" +
                "   5. 您在复制、发行或者公开表演本作品，或者复制、发行或者公开表演作为任何修改作品一部分的本作品时，不得歪曲、损害或者以其他方式损害本作品，导致原始作者的名誉或者荣誉受损。\n" +
                "\n" +
                "   6. 您不得利用本作品的全部或部分申请商业用途的商标和/或专利。\n" +
                "\n" +
                "   7. 作者拥有对本协议的修改权，当您使用本作品、源代码及其附属资源的修改协议后的作品，需遵守最新协议。\n" +
                "\n" +
                "5. 免责声明：\n" +
                "\n" +
                "   1. 您在下载并使用本作品时均被视为已经仔细阅读本协议并完全同意。凡以任何方式使用本作品，或直接、间接使用本作品，均被视为自愿接受相关声明和用户服务协议的约束。\n" +
                "\n" +
                "   2. 除非本协议的当事人相互以书面的方式做出相反约定，且在相关法律所允许的最大范围内，否则作者按其现状提供本作品，对本作品不作任何明示或者默示、依照法律或者其他规定的陈述或担保，包括但是不限于任何有关可否商业性使用、是否符合特定的目的、不具有潜在的或者其他缺陷、准确性或者不存在不论能否被发现的错误的担保。有些司法管辖区不允许排除前述默示保证，因此这些排除性规定并不一定适用于您。\n" +
                "\n" +
                "   3. 用户明确并同意其使用本作品所存在的风险及法律风险将完全由其本人承担；因其使用作品而产生的一切后果也由其本人承担，本作品作者对此不承担任何责任。\n" +
                "\n" +
                "   6. 除非书面同意，否则在任何情况下，任何作者与协议作者，或经其修改和/或传送上述程序的任何其他方均不对您承担赔偿责任，包括任何一般的，特殊的，因本作品而使您对其他法律实体造成的一切损害。本作品及作者已提前告知您此类损害的可能性。\n" +
                "\n" +
                "   7. 您在传播、使用本作品及其修改作品时，应自行保证您的一切行为与本作品的全部功能符合一切对您有管辖权的法律法规的要求，由您传播、使用本作品产生的法律风险及其造成的相应后果，将由您自行承担，本作品及其作者不承担任何责任。\n" +
                "\n" +
                "   8. 本协议最终解释权归本作品作者与协议作者所有。\n" +
                "\n" +
                "\n" +
                "6. 许可终止：\n" +
                "\n" +
                "   1. 在您违反本协议协议任何条款时，本协议及其所授予的权利将自动终止。然而，根据本协议从您处获取修改作品的自然人、法人或者其他组织，如果他们仍完全遵守相关条款，则对他们的许可不会随之终止。即使本协议被终止，第 1 款、第 2 款、第 5 款、第 6 款仍然有效。\n" +
                "\n" +
                "   2. 在上述条款及条件的前提下，此处授予的许可在法定著作权保护期限内有效。即便如此，作者保留依其他许可条款发行本作品及在任何时候停止发行本作品的权利；但是，作者的上述权利不能被用于撤销本协议或任何其他在本协议条款下授予的或必须授予的许可，除本款第 1 项指明的终止外，本协议将保持其完全效力。";
    }

    public static class HostInfo {
        public static class ZuiyouLite {
            public static final String PACKAGE_NAME = "cn.xiaochuankeji.zuiyouLite";
            public static final int PP_2_45_10 = 2451000;
            public static final int PP_2_46_0 = 2460000;
            public static final int PP_2_46_1 = 2460100;
            public static final int PP_2_47_1 = 2470100;
            public static final int PP_2_47_10 = 2471000;
            public static final int PP_2_48_10 = 2481000;
            public static final int PP_2_49_10 = 2491000;
            public static final int PP_2_50_10 = 2501000;
            public static final int PP_2_52_11 = 2521100;
            public static final int PP_2_60_11 = 2601100;
        }

        public static class TieBa {
            public static final String PACKAGE_NAME = "cn.xiaochuankeji.tieba";
            public static final int ZY_5_11_20 = 511200;
        }

    }
}
