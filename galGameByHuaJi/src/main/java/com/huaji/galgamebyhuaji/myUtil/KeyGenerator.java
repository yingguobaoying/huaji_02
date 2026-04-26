package com.huaji.galgamebyhuaji.myUtil;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 密钥生成器
 * todo :请勿在正式上线时启用简单的发癫密钥,而是使用另一个高强度的安全密钥
 * todo :千万别用错了哦~~
 */
public class KeyGenerator {
	//诶嘿嘿.....红豆老婆~~~
	private static final String SOURCE_TEXT =
			"自从我第一次在游戏中遇见红豆，她那双闪亮的大眼睛和俏皮的红发就深深地烙印在我的心里。每当她出现在屏幕上，我的心情就会变得异常激动，仿佛中了某种神秘的魔法红豆，这个名字已经成为了我游戏生活中不可或缺的一部分，她让我体验到了前所未有的快乐和痴迷每当红豆在游戏中展开她的冒险每一次攻击，都让我心跳加速，仿佛我也置身于那个充满奇幻色彩的世界。红豆的每一个动作，都像是一剂强心针，让我对这个游戏爱得无法自拔红豆的笑声是那么地感染力，每当她开心地笑出声，我的世界仿佛都被这笑声照亮。她的笑容，就像是游戏里的阳光，让我感受到了无尽的温暖。红豆的忧愁，我也会感同身受，她的喜怒哀乐已经成为了我情绪的晴雨表在游戏的世界里，红豆是我心中的女主角，她的故事线让我如痴如醉。每当她遇到困难，我都会为她紧张得手心冒汗；而当她战胜敌人，我会为她欢呼雀跃红豆的成长历程，仿佛就是我自己的成长历程，我们一同在游戏的世界里砥砺前行红豆的每一个造型，我都会细细品味，她的服装、道具，甚至是她的发型，都成为了我研究的对象。我会在游戏之外的时间里，想象红豆的故事，为她编织一个又一个美好的梦境。红豆，已经不仅仅是一个游戏角色，她是我心中的一个美好寄托有时候，我会对着屏幕发呆，想象着自己能和红豆一起并肩作战，共同探索那个未知的世界。她的勇敢、善良和坚韧，让我为之倾倒。红豆，就像是我心中的一个榜样，让我在现实生活中也学会了勇敢面对困难我知道，这种对红豆的喜爱已经近乎病态，但我不愿醒来在这个虚拟的世界里，红豆给了我太多美好的回忆她让我感受到了游戏的魅力，也让我明白了什么是真正的热爱红豆，就像是我心中的一个梦，我愿意一直沉浸在这个梦中，不愿醒来或许在别人看来，我对红豆的喜爱是一种疯狂，但只有我自己知道，这份热爱是多么地纯粹在这个充满压力和挑战的现实世界，红豆成为了我心灵的避风港，她让我在游戏的世界里找到了属于自己的快乐红豆这个游戏人物已经深深地融入了我的生活，她让我感受到了前所未有的快乐和激情我对她的喜爱，如同发病般无法自拔在这片虚拟的土地上，红豆是我永远的信仰，我会一直陪伴她，走过每一个游戏世界的角落";
	
	/**
	 * 生成密钥
	 *
	 * @param size 预期的最小长度（必须 >= 100）
	 * @return 拼接后的字符串，长度介于 [size, 2*size]
	 */
	public static String generateCrazyKey(int size) {
		if (size < 100) {
			throw new IllegalArgumentException("size 必须 >= 100");
		}
		
		// 切分句子：根据标点或换行符
		String[] rawSentences = SOURCE_TEXT.split("[。！？!?.\\n]");
		List<String> sentences = new ArrayList<>();
		for (String s : rawSentences) {
			s = s.trim();
			if (!s.isEmpty()) {
				sentences.add(s);
			}
		}
		
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		
		// 目标范围：长度在 [size, 2*size] 之间
		int maxLength = size * 2;
		while (sb.length() < size) {
			String fragment = sentences.get(random.nextInt(sentences.size()));
			sb.append(fragment);
			sb.append(getRandomPunctuation(random)); // 添加一点随机标点调味
		}
		
		// 如果超出最大长度则截断
		if (sb.length() > maxLength) {
			return sb.substring(0, maxLength);
		}
		
		return sb.toString();
	}
	
	// 随机添加句末标点
	private static String getRandomPunctuation(Random random) {
		String[] punctuations = {"。", "？", "！！", "？？", "。。。。。。", "《神经》", "，你是一个一个", "。1145141919810", "（疯）", "。怎么那里都有homo啊！！！", "红豆可爱滴捏"};		return punctuations[random.nextInt(punctuations.length)];
	}
	
	public static void main(String[] args) {
		System.out.println("**************************发癫密钥生成器开始工作******************************************");
		System.err.println("不推荐用于生成环境,千万别用错了哦~~");
		for (int i = 0; i < 100; i++) {
			String crazyKey = generateCrazyKey(120);
			System.out.println("生成的发癫密钥：");
			System.out.println(crazyKey);
			System.out.println("长度: " + crazyKey.length());
		}
		System.err.println("不推荐用于生成环境,千万别用错了哦~~");
		System.out.println("**************************发癫密钥生成器工作结束******************************************");
		
	}
}

/**
 * UTF-8随机密钥生成器
 * 生成符合Unicode标准的随机字符串，排除控制字符和特殊字符
 */
class SafeKeyGenerator {
	
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	
	// Unicode字符范围定义
	//请注意如果使用不支持UTF-8的系统,请注意修改
	private static final int[] SAFE_UNICODE_RANGES = {
			// 基本多文种平面（BMP）安全字符范围
			0x0020, 0x007E,   // 基本拉丁字符（空格到~）
			0x00A0, 0x00FF,   // 拉丁文补充-1
			0x0100, 0x017F,   // 拉丁文扩展-A
			0x0180, 0x024F,   // 拉丁文扩展-B
			0x0250, 0x02AF,   // 国际音标扩展
			0x02B0, 0x02FF,   // 间距修饰字母
			0x0300, 0x036F,   // 组合用附加符号
			0x0370, 0x03FF,   // 希腊字母及科普特字母
			0x0400, 0x04FF,   // 西里尔字母
			0x0500, 0x052F,   // 西里尔字母补充
			0x0530, 0x058F,   // 亚美尼亚字母
			0x0590, 0x05FF,   // 希伯来字母
			0x0600, 0x06FF,   // 阿拉伯字母
			0x0700, 0x074F,   // 叙利亚字母
			0x0750, 0x077F,   // 阿拉伯字母补充
			0x0780, 0x07BF,   // 它拿字母
			0x07C0, 0x07FF,   // 西非书面文字
			0x0800, 0x083F,   // 撒玛利亚字母
			0x0840, 0x085F,   // 曼达字母
			0x0860, 0x086F,   // 叙利亚字母补充
			0x08A0, 0x08FF,   // 阿拉伯字母扩展-A
			0x0900, 0x097F,   // 天城文
			0x0980, 0x09FF,   // 孟加拉文
			0x0A00, 0x0A7F,   // 古木基文
			0x0A80, 0x0AFF,   // 古吉拉特文
			0x0B00, 0x0B7F,   // 奥里亚文
			0x0B80, 0x0BFF,   // 泰米尔文
			0x0C00, 0x0C7F,   // 泰卢固文
			0x0C80, 0x0CFF,   // 卡纳达文
			0x0D00, 0x0D7F,   // 马拉雅拉姆文
			0x0D80, 0x0DFF,   // 僧伽罗文
			0x0E00, 0x0E7F,   // 泰文
			0x0E80, 0x0EFF,   // 老挝文
			0x0F00, 0x0FFF,   // 藏文
			0x1000, 0x109F,   // 缅甸文
			0x10A0, 0x10FF,   // 格鲁吉亚字母
			0x1100, 0x11FF,   // 谚文字母
			0x1200, 0x137F,   // 埃塞俄比亚字母
			0x1380, 0x139F,   // 埃塞俄比亚字母补充
			0x13A0, 0x13FF,   // 切罗基字母
			0x1400, 0x167F,   // 统一加拿大原住民音节文字
			0x1680, 0x169F,   // 欧甘字母
			0x16A0, 0x16FF,   // 如尼文
			0x1700, 0x171F,   // 他加禄字母
			0x1720, 0x173F,   // 哈努诺文
			0x1740, 0x175F,   // 布希德文
			0x1760, 0x177F,   // 塔格班瓦文
			0x1780, 0x17FF,   // 高棉文
			0x1800, 0x18AF,   // 蒙古文
			0x18B0, 0x18FF,   // 统一加拿大原住民音节文字扩展
			0x1900, 0x194F,   // 林布文
			0x1950, 0x197F,   // 德宏傣文
			0x1980, 0x19DF,   // 新傣仂文
			0x19E0, 0x19FF,   // 高棉符号
			0x1A00, 0x1A1F,   // 布吉文
			0x1A20, 0x1AAF,   // 老傣文
			0x1AB0, 0x1AFF,   // 组合用附加符号扩展
			0x1B00, 0x1B7F,   // 巴厘字母
			0x1B80, 0x1BBF,   // 巽他字母
			0x1BC0, 0x1BFF,   // 巴塔克文
			0x1C00, 0x1C4F,   // 雷布查字母
			0x1C50, 0x1C7F,   // 桑塔利文
			0x1C80, 0x1C8F,   // 西里尔字母扩展-C
			0x1C90, 0x1CBF,   // 格鲁吉亚字母扩展
			0x1CC0, 0x1CCF,   // 巽他字母补充
			0x1CD0, 0x1CFF,   // 吠陀扩展
			0x1D00, 0x1D7F,   // 音标扩展
			0x1D80, 0x1DBF,   // 音标扩展补充
			0x1DC0, 0x1DFF,   // 组合用附加符号补充
			0x1E00, 0x1EFF,   // 拉丁文扩展附加
			0x1F00, 0x1FFF,   // 希腊字母扩展
			0x2000, 0x206F,   // 常用标点
			0x2070, 0x209F,   // 上标及下标
			0x20A0, 0x20CF,   // 货币符号
			0x20D0, 0x20FF,   // 组合用符号
			0x2100, 0x214F,   // 字母式符号
			0x2150, 0x218F,   // 数字形式
			0x2190, 0x21FF,   // 箭头
			0x2200, 0x22FF,   // 数学运算符
			0x2300, 0x23FF,   // 杂项技术符号
			0x2400, 0x243F,   // 控制图片
			0x2440, 0x245F,   // 光学识别符
			0x2460, 0x24FF,   // 带圈或带括号的字母数字
			0x2500, 0x257F,   // 制表符
			0x2580, 0x259F,   // 方块元素
			0x25A0, 0x25FF,   // 几何图形
			0x2600, 0x26FF,   // 杂项符号
			0x2700, 0x27BF,   // 装饰符号
			0x27C0, 0x27EF,   // 杂项数学符号-A
			0x27F0, 0x27FF,   // 追加箭头-A
			0x2800, 0x28FF,   // 盲文点字模型
			0x2900, 0x297F,   // 追加箭头-B
			0x2980, 0x29FF,   // 杂项数学符号-B
			0x2A00, 0x2AFF,   // 追加数学运算符
			0x2B00, 0x2BFF,   // 杂项符号和箭头
			0x2C00, 0x2C5F,   // 格拉哥里字母
			0x2C60, 0x2C7F,   // 拉丁文扩展-C
			0x2C80, 0x2CFF,   // 科普特字母
			0x2D00, 0x2D2F,   // 格鲁吉亚字母补充
			0x2D30, 0x2D7F,   // 提非纳文
			0x2D80, 0x2DDF,   // 埃塞俄比亚字母扩展
			0x2DE0, 0x2DFF,   // 西里尔字母扩展-A
			0x2E00, 0x2E7F,   // 追加标点
			0x2E80, 0x2EFF,   // 中日韩部首补充
			0x2F00, 0x2FDF,   // 康熙部首
			0x2FF0, 0x2FFF,   // 表意文字描述字符
			0x3000, 0x303F,   // 中日韩符号和标点
			0x3040, 0x309F,   // 日文平假名
			0x30A0, 0x30FF,   // 日文片假名
			0x3100, 0x312F,   // 注音字母
			0x3130, 0x318F,   // 谚文兼容字母
			0x3190, 0x319F,   // 象形字注释标志
			0x31A0, 0x31BF,   // 注音字母扩展
			0x31C0, 0x31EF,   // 中日韩笔画
			0x31F0, 0x31FF,   // 日文片假名语音扩展
			0x3200, 0x32FF,   // 带圈中日韩字母和月份
			0x3300, 0x33FF,   // 中日韩兼容字符
			0x3400, 0x4DBF,   // 中日韩统一表意文字扩展A
			0x4DC0, 0x4DFF,   // 易经六十四卦符号
			0x4E00, 0x9FFF,   // 中日韩统一表意文字
			0xA000, 0xA48F,   // 彝文音节
			0xA490, 0xA4CF,   // 彝文字根
			0xA4D0, 0xA4FF,   // 老傈僳文
			0xA500, 0xA63F,   // 瓦伊语
			0xA640, 0xA69F,   // 西里尔字母扩展-B
			0xA6A0, 0xA6FF,   // 巴姆穆文字
			0xA700, 0xA71F,   // 声调修饰字母
			0xA720, 0xA7FF,   // 拉丁文扩展-D
			0xA800, 0xA82F,   // 锡尔赫特文
			0xA830, 0xA83F,   // 通用印度数字格式
			0xA840, 0xA87F,   // 八思巴文
			0xA880, 0xA8DF,   // 索拉什特拉文
			0xA8E0, 0xA8FF,   // 天城文扩展
			0xA900, 0xA92F,   // 克耶文
			0xA930, 0xA95F,   // 勒姜文
			0xA960, 0xA97F,   // 谚文扩展-A
			0xA980, 0xA9DF,   // 爪哇文
			0xA9E0, 0xA9FF,   // 缅甸文扩展-B
			0xAA00, 0xAA5F,   // 占文
			0xAA60, 0xAA7F,   // 缅甸文扩展-A
			0xAA80, 0xAADF,   // 越南傣文
			0xAAE0, 0xAAFF,   // 梅泰文扩展
			0xAB00, 0xAB2F,   // 埃塞俄比亚字母扩展-A
			0xAB30, 0xAB6F,   // 拉丁文扩展-E
			0xAB70, 0xABBF,   // 切罗基文补充
			0xABC0, 0xABFF,   // 梅泰文
			0xAC00, 0xD7AF,   // 谚文音节
			0xD7B0, 0xD7FF,   // 谚文扩展-B
			0xF900, 0xFAFF,   // 中日韩兼容表意文字
			0xFB00, 0xFB4F,   // 字母表达形式
			0xFB50, 0xFDFF,   // 阿拉伯表达形式-A
			0xFE00, 0xFE0F,   // 变体选择符
			0xFE10, 0xFE1F,   // 竖排形式
			0xFE20, 0xFE2F,   // 组合用半符号
			0xFE30, 0xFE4F,   // 中日韩兼容形式
			0xFE50, 0xFE6F,   // 小写变体形式
			0xFE70, 0xFEFF,   // 阿拉伯表达形式-B
			0xFF00, 0xFFEF,   // 半形及全形字符
			0xFFF0, 0xFFFF,   // 特殊
	};
	
	// 排除的危险字符范围（控制字符、不可见字符、特殊符号等）
	private static final int[][] EXCLUDED_RANGES = {
			{0x0000, 0x001F},   // C0控制字符
			{0x007F, 0x009F},   // C1控制字符
			{0x2000, 0x200F},   // 各种空格和不可见字符
			{0x2028, 0x202F},   // 行分隔符等
			{0x205F, 0x206F},   // 各种不可见字符
			{0xFEFF, 0xFEFF},   // 零宽不折行空格
			{0xFFF0, 0xFFF8},   // 特殊用途
			{0xE000, 0xF8FF},   // 私有区域
			{0xFFF9, 0xFFFD},   // 特殊用途
			{0x2E00, 0x2E7F},   // 追加标点（部分排除）
	};
	
	// 预计算的安全字符总数和范围索引
	private static final long TOTAL_SAFE_CHARACTERS;
	private static final List<int[]> SAFE_RANGES = new ArrayList<>();
	
	static {
		// 初始化安全字符范围
		initializeSafeRanges();
		TOTAL_SAFE_CHARACTERS = calculateTotalSafeCharacters();
	}
	
	/**
	 * 生成高强度随机密钥
	 *
	 * @param length 密钥长度（字符数）
	 * @return 标准化后的UTF-8字符串
	 */
	public static String generateSecureKey(int length) {
		return generateSecureKey(length, true);
	}
	
	/**
	 * 生成高强度随机密钥
	 *
	 * @param length    密钥长度（字符数）
	 * @param normalize 是否进行Unicode标准化
	 * @return UTF-8 字符串密钥
	 */
	public static String generateSecureKey(int length, boolean normalize) {
		if (length <= 0) {
			throw new IllegalArgumentException("密钥长度必须大于0");
		}
		
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int codePoint = getRandomSafeCodePoint();
			sb.appendCodePoint(codePoint);
		}
		
		String result = sb.toString();
		return normalize ? Normalizer.normalize(result, Normalizer.Form.NFC) : result;
	}
	
	/**
	 * 获取随机安全码点
	 */
	private static int getRandomSafeCodePoint() {
		long randomPosition = (long) (SECURE_RANDOM.nextDouble() * TOTAL_SAFE_CHARACTERS);
		return findCodePointByPosition(randomPosition);
	}
	
	/**
	 * 根据位置查找对应的码点
	 */
	private static int findCodePointByPosition(long position) {
		long current = 0;
		for (int[] range : SAFE_RANGES) {
			int start = range[0];
			int end = range[1];
			long rangeSize = end - start + 1;
			
			if (position < current + rangeSize) {
				return start + (int) (position - current);
			}
			current += rangeSize;
		}
		throw new IllegalStateException("安全字符范围计算错误");
	}
	
	/**
	 * 初始化安全字符范围
	 */
	private static void initializeSafeRanges() {
		// 处理包含范围
		for (int i = 0; i < SAFE_UNICODE_RANGES.length; i += 2) {
			int start = SAFE_UNICODE_RANGES[i];
			int end = SAFE_UNICODE_RANGES[i + 1];
			SAFE_RANGES.add(new int[]{start, end});
		}
		
		// 排除危险范围
		for (int[] excludedRange : EXCLUDED_RANGES) {
			int excludeStart = excludedRange[0];
			int excludeEnd = excludedRange[1];
			
			List<int[]> newRanges = new ArrayList<>();
			for (int[] safeRange : SAFE_RANGES) {
				int safeStart = safeRange[0];
				int safeEnd = safeRange[1];
				
				if (excludeEnd < safeStart || excludeStart > safeEnd) {
					// 无重叠，保留原范围
					newRanges.add(safeRange);
				}
				else {
					// 有重叠，分割范围
					if (safeStart < excludeStart) {
						newRanges.add(new int[]{safeStart, excludeStart - 1});
					}
					if (excludeEnd < safeEnd) {
						newRanges.add(new int[]{excludeEnd + 1, safeEnd});
					}
				}
			}
			SAFE_RANGES.clear();
			SAFE_RANGES.addAll(newRanges);
		}
	}
	
	/**
	 * 计算总安全字符数
	 */
	private static long calculateTotalSafeCharacters() {
		return SAFE_RANGES.stream()
				.mapToLong(range -> range[1] - range[0] + 1L)
				.sum();
	}
	
	/**
	 * 估算密钥熵值（比特）
	 */
	public static double estimateEntropy(int length) {
		return length * (Math.log(TOTAL_SAFE_CHARACTERS) / Math.log(2));
	}
	
	/**
	 * 验证字符串是否只包含安全字符
	 */
	public static boolean isSafeString(String input) {
		return input.codePoints()
				.allMatch(SafeKeyGenerator::isSafeCodePoint);
	}
	
	/**
	 * 检查单个码点是否安全
	 */
	public static boolean isSafeCodePoint(int codePoint) {
		for (int[] range : SAFE_RANGES) {
			if (codePoint >= range[0] && codePoint <= range[1]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取安全字符统计信息
	 */
	public static Map<String, Object> getSecurityInfo() {
		Map<String, Object> info = new LinkedHashMap<>();
		info.put("totalSafeCharacters", TOTAL_SAFE_CHARACTERS);
		info.put("entropyPerCharacter", Math.log(TOTAL_SAFE_CHARACTERS) / Math.log(2));
		info.put("safeRangesCount", SAFE_RANGES.size());
		info.put("recommendedMinLength", 12); // 推荐最小长度
		return info;
	}
	
	public static void main(String[] args) {
		int size = 5;
		System.out.println("**************************密钥生成器开始工作******************************************");
		System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
		System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
		for (int i = 0; i < size; i++) System.out.println(generateSecureKey(100));
		System.out.println("**************************发癫密钥生成器工作结束******************************************");
	}
}