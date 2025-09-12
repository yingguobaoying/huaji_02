package com.huaji.galgamebyhuaji.myUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 密钥生成器
 * todo :请勿在正式上线时启用简单的发癫密钥,而是使用另一个高强度的安全密钥,这里是懒得删了
 */
public class CrazyKeyGenerator {
	
	// 文学源文本
	private static final String SOURCE_TEXT =
			"自从我第一次在游戏中遇见红豆，她那双闪亮的大眼睛和俏皮的红发就深深地烙印在我的心里。l每当她出现在屏幕上，我的心情就会变得异常激动，仿佛中了某种神秘的魔法红豆，这个名字已经成为了我游戏生活中不可或缺的一部分，她让我体验到了前所未有的快乐和痴迷每当红豆在游戏中展开她的冒险每一次攻击，都让我心跳加速，仿佛我也置身于那个充满奇幻色彩的世界。红豆的每一个动作，都像是一剂强心针，让我对这个游戏爱得无法自拔红豆的笑声是那么地感染力，每当她开心地笑出声，我的世界仿佛都被这笑声照亮。她的笑容，就像是游戏里的阳光，让我感受到了无尽的温暖。红豆的忧愁，我也会感同身受，她的喜怒哀乐已经成为了我情绪的晴雨表在游戏的世界里，红豆是我心中的女主角，她的故事线让我如痴如醉。每当她遇到困难，我都会为她紧张得手心冒汗；而当她战胜敌人，我会为她欢呼雀跃红豆的成长历程，仿佛就是我自己的成长历程，我们一同在游戏的世界里砥砺前行红豆的每一个造型，我都会细细品味，她的服装、道具，甚至是她的发型，都成为了我研究的对象。我会在游戏之外的时间里，想象红豆的故事，为她编织一个又一个美好的梦境。红豆，已经不仅仅是一个游戏角色，她是我心中的一个美好寄托有时候，我会对着屏幕发呆，想象着自己能和红豆一起并肩作战，共同探索那个未知的世界。她的勇敢、善良和坚韧，让我为之倾倒。红豆，就像是我心中的一个榜样，让我在现实生活中也学会了勇敢面对困难我知道，这种对红豆的喜爱已经近乎病态，但我不愿醒来在这个虚拟的世界里，红豆给了我太多美好的回忆她让我感受到了游戏的魅力，也让我明白了什么是真正的热爱红豆，就像是我心中的一个梦，我愿意一直沉浸在这个梦中，不愿醒来或许在别人看来，我对红豆的喜爱是一种疯狂，但只有我自己知道，这份热爱是多么地纯粹在这个充满压力和挑战的现实世界，红豆成为了我心灵的避风港，她让我在游戏的世界里找到了属于自己的快乐红豆这个游戏人物已经深深地融入了我的生活，她让我感受到了前所未有的快乐和激情我对她的喜爱，如同发病般无法自拔在这片虚拟的土地上，红豆是我永远的信仰，我会一直陪伴她，走过每一个游戏世界的角落";
	
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
		String[] punctuations = {"。", "？", "！！", "？？", "。。。。。。", "《神经》", "，你是一个一个", "。1145141919810", "（疯）", "。怎么那里都有homo啊！！！", "红豆可爱滴捏"};
		return punctuations[random.nextInt(punctuations.length)];
	}
	
	// 测试主方法
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			String crazyKey = generateCrazyKey(120);
			System.out.println("生成的发癫密钥：");
			System.out.println(crazyKey);
			System.out.println("长度: " + crazyKey.length());
		}
	}
}
