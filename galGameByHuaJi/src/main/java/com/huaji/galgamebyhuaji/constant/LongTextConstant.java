package com.huaji.galgamebyhuaji.constant;

public class LongTextConstant {
	public static void main(String[] args) {
		System.out.println(getReturnPage("title", "content"));
		System.out.println(getEmailText("title", "content", "url", "123456789@xx.xxx"));
		System.out.println(getEmailCopyText("title", "content", "url", "123456789@xx.xxx"));
	}
	
	//==========================按钮验证跳转模板====================================
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
                   <p>如果您未申请过此操作，您可以忽略此邮件。不过为保证您的账户安全，请及时检查您的账号情况。</p>
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
	
	//==========================复制内容跳转模板====================================
	
	public static String getEmailCopyText(String title, String content, String email, String copyText) {
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
		                    .copy-wrapper {
		                      position: relative;
		                      margin: 20px 0;
		                    }
		                    .copyText {
		                      margin: 10px 0;
		                      padding: 12px;
		                      background-color: #f8f9fa;
		                      border: 1px solid #e9ecef;
		                      border-radius: 4px;
		                      word-break: break-all;
		                      font-family: monospace;
		                      font-size: 14px;
		                      cursor: text;
		                      user-select: all;
		                      -webkit-user-select: all;
		                      -moz-user-select: all;
		                      -ms-user-select: all;
		                    }
		                    .copyText:focus {
		                      outline: 2px solid #0077cc;
		                      background-color: #fff;
		                    }
		                    .copy-hint {
		                      display: none;
		                      position: absolute;
		                      top: -30px;
		                      left: 50%%;
		                      transform: translateX(-50%%);
		                      background: #333;
		                      color: white;
		                      padding: 5px 10px;
		                      border-radius: 4px;
		                      font-size: 12px;
		                      white-space: nowrap;
		                    }
		                    .copyText:focus + .copy-hint {
		                      display: block;
		                    }
		                    .manual-copy-instruction {
		                      font-size: 12px;
		                      color: #666;
		                      margin-top: 5px;
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
		                    <p>%s</p>
		                    <div class="copy-wrapper">
		                      <div class="copyText" id="copyText" tabindex="0">%s</div>
		                      <div class="copy-hint">内容已选中，按 Ctrl+C 复制</div>
		                    </div>
		                    <p class="manual-copy-instruction">请选中上方内容，然后按 Ctrl+C (Windows) 或 Cmd+C (Mac) 复制</p>
		                    <p>请勿向他人泄露此邮件，本站不会收取任何费用，也没有投放任何广告。</p>
		                    <p>如果您未申请过此操作，您可以忽略此邮件。不过为保证您的账户安全，请及时检查您的账号情况。</p>
		                    <div class="contact">
		                      <p>如有问题，请联系官方邮箱：%s</p>
		                    </div>
		                  </div>
		                </body>
		                </html>
         """.formatted(title, content, copyText, email);
	}
	
	//==========================简单确认窗口跳转模板====================================
	
	/**
	 * 用于获取简单操作结果返回值
	 *
	 * @param title   标题
	 * @param content 内容
	 *
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
