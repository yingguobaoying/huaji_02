package com.huaji.galgamebyhuaji.constant;

public class LongTextConstant {
	public static void main(String[] args) {
		System.out.println(getReturnPage("title", "content"));
		System.out.println(getEmailText("title", "content","url","123456789@xx.xxx"));
		
	}
	
	public static String getEmailText(String title, String content, String url, String email) {
		return """
         <!DOCTYPE html>
         <html lang="zh-CN">
         <head>
           <meta charset="UTF-8">
           <title>邮箱验证</title>
           <style>
             body {
               font-family: Arial, sans-serif;
               text-align: center;
               padding: 50px;
               background: #f5f5f5;
             }
             .container {
               background: #fff;
               padding: 40px;
               max-width: 400px;
               margin: auto;
               border-radius: 8px;
               box-shadow: 0 0 10px rgba(0,0,0,0.1);
             }
             h1 {
               margin-bottom: 20px;
               color: #333;
             }
             p {
               color: #555;
               font-size: 14px;
               line-height: 1.6;
             }
             a.button {
               display: inline-block;
               margin-top: 20px;
               padding: 12px 24px;
               background-color: #0077cc;
               color: #fff;
               text-decoration: none;
               border-radius: 6px;
               font-size: 16px;
             }
             a.button:hover {
               background-color: #005fa3;
             }
             .contact {
               margin-top: 30px;
               font-size: 13px;
               color: #777;
             }
           </style>
         </head>
         <body>
           <div class="container">
             <h1>%s</h1>
             <p>%s<br>
                如果按钮无法点击，请将链接复制到浏览器打开。</p>
             <a href="%s" class="button">立即验证邮箱</a>
             <p>请勿向他人泄露此链接，本站不会收取任何费用，也没有投放广告。</p>
             <div class="contact">
               <p>如有问题，请联系官方邮箱：%s</p>
             </div>
           </div>
         </body>
         </html>
         """.formatted(
				title, content, url, email
		);
	}
	
	/**
	 * 用于获取简单操作结果返回值
	 *
	 * @param title   标题
	 * @param content 内容
	 * @return 一个简单的html页面
	 */
	public static String getReturnPage(String title, String content) {
		return """
         <!doctype html>
         <html lang="zh-CN">
         <head>
           <meta charset="utf-8" />
           <meta name="viewport" content="width=device-width,initial-scale=1" />
           <title>操作完成</title>
           <style>
              body {
               font-family: Arial, sans-serif;
               text-align: center;
               padding: 50px;
               background: #f5f5f5;
             }
             .container {
               background: #fff;
               padding: 40px;
               max-width: 400px;
               margin: auto;
               border-radius: 8px;
               box-shadow: 0 0 10px rgba(0,0,0,0.1);
             }
             h1 {
               margin-bottom: 20px;
               color: #333;
             }
             p {
               color: #555;
               font-size: 14px;
               line-height: 1.6;
             }
             a.button {
               display: inline-block;
               margin-top: 20px;
               padding: 12px 24px;
               background-color: #0077cc;
               color: #fff;
               text-decoration: none;
               border-radius: 6px;
               font-size: 16px;
             }
             a.button:hover {
               background-color: #005fa3;
             }
             .contact {
               margin-top: 30px;
               font-size: 13px;
               color: #777;
             }
           </style>
         </head>
         <body>
           <div class="wrap">
             <div class="card">
               <h1 id="result-title">%s</h1>
               <p class="desc">%s</p>
               <p class="desc">现在您可以安全关闭此窗口</p>
               <div class="countdown">页面将在 <span id="seconds">5</span> 秒后关闭</div>
             </div>
           </div>
           <script>
             var seconds = 5;
             var secondsEl = document.getElementById('seconds');
             function doClose(){
               try{ window.close(); }catch(e){ alert("窗口关闭失败，请手动关闭"); }
               if(history.length>1){ history.back(); }
             }
             var timer = setInterval(function(){
               seconds -= 1;
               if(seconds < 0){
                 clearInterval(timer);
                 doClose();
                 return;
               }
               secondsEl.textContent = seconds;
             },1000);
           </script>
         </body>
         </html>
         """.formatted(title, content);
	}
}
