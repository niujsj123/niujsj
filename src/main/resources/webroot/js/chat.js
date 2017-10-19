/**
 * 聊天程序
 */
// 扩展一个date对象的format方法
Date.prototype.format=function(format){
	var d = {
		"M+" : this.getMonth() +1, // 月
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth()+3)/3), // 刻
		"S" : this.getMilliseconds() // 毫秒
	}
	if(/(y+)/.test(format)){
		format = format.replace(RegExp.$1,(this.getFullYear()+"").substring(4- RegExp.$1.length));
	}
	
	for (var k in d){
		if (new RegExp("("+ k +")").test(format)){
			format = format.replace(RegExp.$1,RegExp.$1.length==1?d[k]:("00"+d[k]).substring((""+d[k]).length));
		}
	}
	return format;
};
// $(function() {
	function send() {
// $("#send-message").empty();
	$("#upload").click(); // 隐藏了input:file样式后，点击头像就可以本地上传
// $("#upload").on("change",function(){
// var path = getObjectURL(this.files[0]) ; //获取图片的路径，该路径不是图片在本地的路径
// if (path) {
// var img = '<img src="'+path+'"/>';
// $("#send-message").html(img);
// $("#send-message").focus();
// this.files[0] =null;
// }
// });
	};
// });
	 
	// 建立一個可存取到該file的url
	function getObjectURL(file) {
	var url = null ;
	if (window.createObjectURL!=undefined) { // basic
	url = window.createObjectURL(file) ;
	} else if (window.URL!=undefined) { // mozilla(firefox)
	url = window.URL.createObjectURL(file) ;
	} else if (window.webkitURL!=undefined) { // webkit or chrome
	url = window.webkitURL.createObjectURL(file) ;
	}
	return url ;
	}
$(document).ready(function(){
	// alert("jquery");
	var host = location.host;
	var name =null;
	$("#pic").click(function () {
		$("#upload").click(); // 隐藏了input:file样式后，点击头像就可以本地上传
		$("#upload").on("change",function(){
			if (name != $("#upload")[0].value){
				var formData = new FormData();
				name = $("#upload")[0].value;
				formData.append("file",$("#upload")[0].files[0]);
				formData.append("name",name);
				$.ajax({ 
				url : "http://192.168.20.105/upload", 
				type : 'POST', 
				data : formData, 
				// 告诉jQuery不要去处理发送的数据
				processData : false, 
				// 告诉jQuery不要去设置Content-Type请求头
				contentType : false,
				beforeSend:function(){
				console.log("正在进行，请稍候");
				},
				success : function(responseStr) { 
				
				var regx= /.+(jpg|png|gif|ico)$/;
				
				var img = null;
				if(!regx.test(responseStr)){
					console.log("成功"+responseStr);
					var imgStr = "http://192.168.20.105/126.gif";
					img = "<img src='"+imgStr+"' onclick=window.open('"+responseStr+"')"+">";
				} else {
					img = '<img src="'+responseStr+'" onclick="window.open(this.src)"/>';
				}
				$("#send-message").append(img);
				$("#send-message").focus();
				}, 
				error : function(responseStr) { 
				console.log("error");
				} 
				});
			}});
		});
	
	function getObjectURL(file) {
		var url = null ;
		if (window.createObjectURL!=undefined) { // basic
		url = window.createObjectURL(file) ;
		} else if (window.URL!=undefined) { // mozilla(firefox)
		url = window.URL.createObjectURL(file) ;
		} else if (window.webkitURL!=undefined) { // webkit or chrome
		url = window.webkitURL.createObjectURL(file) ;
		}
		return url ;
		}
	// alert(host);
	// 写一个客户端对象
	window.CHAT ={
		serverAddr : "ws://"+ host+"/im",
		// 保存客户端Socket对象
		socket : null,
		// 保存用户的昵称
		nickname : null,
		// 登录方法
		login: function(){
			$("#error-msg").empty();
			var reg = /^S{1,10}/;
			var nickname = $("#nickname").val();
			if (reg.test($.trim(nickname))){
				$("#error-msg").html("昵称长度必须在10个字以内");
				return false;
			}
			$("#nickname").val("");
			$("#loginbox").hide();
			$("#chatbox").show();
			this.init(nickname);
		},
		logout: function(){
			location.reload();
		},
		// 添加系统提示
		// 动态创建一个HTML元素
		addSystemTip:function(content){
			var html = "";
			html += '<div class="msg-system">';
			html += content;
			html += "</div>";
			var section = document.createElement('section');
			section.className = 'system J-mjrlinkWrap J-cutMsg';
			section.innerHTML = html;
			
			$("#online-msg").append(section);
			
		},
		// 选择表情
		openFace:function(){
			var box = $("#face-box");
			// 避免重复打开表情选择框
			if (box.hasClass("open")){
				box.hide();
				box.removeClass("open");
				return;
			}
			box.addClass("open");
			box.show();
			
			if (box.html()!="")return;
			var faceIcon ="";
			for(var i=1;i<=130;i++){
				var path="/images/face/"+i+".gif";
				faceIcon+='<span class="face-item" onclick="CHAT.selectFace(\''+path+'\')">';
				faceIcon+= '<img src="'+path+'"/>';
				faceIcon+="</span>";
			}
			box.html(faceIcon);
		},
		// 选择一张图片
		selectFace:function(path){
			var faceBox = $("#face-box");
			faceBox.hide();
			faceBox.removeClass("open");
			var img = '<img src="'+path+'"/>';
			$("#send-message").append(img);
			$("#send-message").focus();
		},
		sendFlower:function(){
			if (CHAT.socket.readyState ==WebSocket.OPEN){
				var msg = "[FLOWER]["+new Date().getTime()+"]["+CHAT.nickname+"]";
				CHAT.socket.send(msg);
			} else {
				CHAT.addSystemTip("你已处于离线状态，无法发送鲜花");
			}
		},
		sendPic:function(){
			if (CHAT.socket.readyState ==WebSocket.OPEN){
				// $("#upload").click(); //隐藏了input:file样式后，点击头像就可以本地上传
// var f = document.getelementbyid('pic').files[0];
// var src = window.URL.createObjectURL(f);
// var fileName = document.getElementById("pic").value;
// alert(fileName);
// var pic = $("#pic");
// pic.click();
// var str = pic.files[0];//获取文件路径
// alert(str);//在这里你可以进行自己的工作
// var path = window.URL.createObjectURL(str);
				// $("#upload").on("change",function(){
				// var objUrl = getObjectURL(this.files[0]);
				// //获取图片的路径，该路径不是图片在本地的路径
				// if (objUrl) {
					// var img = '<img src="'+path+'"/>';
					// $("#send-message").append(img);
					// $("#send-message").focus();
				// $("#pic").attr("src", objUrl) ; //将图片路径存入src中，显示出图片
				// }
				// });
// var pic = $("#file");
// pic.click();
// var str = pic.value;//获取文件路径
// alert(str);//在这里你可以进行自己的工作
// var img = '<img src="'+path+'"/>';
// $("#send-message").append(img);
// $("#send-message").focus();
			} else {
				CHAT.addSystemTip("你已处于离线状态，无法发送图片");
			}
		},
		// 建立一個可存取到該file的url
		getObjectURL:function(file) {
		var url = null ;
		if (window.createObjectURL!=undefined) { // basic
		url = window.createObjectURL(file) ;
		} else if (window.URL!=undefined) { // mozilla(firefox)
		url = window.URL.createObjectURL(file) ;
		} else if (window.webkitURL!=undefined) { // webkit or chrome
		url = window.webkitURL.createObjectURL(file) ;
		}
		return url ;
		},
		// 发送聊天消息
		sendText:function(){
			var input = $("#send-message");
			if ($.trim(input.html()) == "") return;
			if (CHAT.socket.readyState ==WebSocket.OPEN){
				var msg = "[CHAT]["+new Date().getTime()+"]["+CHAT.nickname+"] - "+input.html().replace(/\n/ig,"<br>");
				CHAT.socket.send(msg);
				input.empty();
				input.focus();
			} else {
				CHAT.addSystemTip("你已处于离线状态，无法发送消息");
			}
		},
		// 将屏幕滚动到最下方
		scrollToBottom:function(){
			window.scrollTo(0,$("#online-msg")[0].scrollHeight);
		},
		// 初始化
		init:function(nickname){
			// 这是初始化赋值
			CHAT.nickname = nickname;
			$("#shownickname").html(nickname);
			
			var message = $("#send-message");
			// 自动获得焦点
			message.focus();
			// 按Ctrl + 回车自动发送消息
			message.keydown(function(e){
				if ((e.ctrlKey && e.which ==13) || e.which ==10){
					CHAT.sendText();
				}
			});
			
			// 将消息添加到聊天面板
			// 专门处理服务端发来的消息
			var appendToPanel = function(msg){
				// 用正则来解析自定义协议
				var regx = /^\[(.*)\](\s\-\s(.*))?/g;
				// 分组， 标签,消息体
				var group = "", header ="",content="",cmd="",time =0,sender="";
				while(group=regx.exec(msg)){
					header = group[1];
					content = group[3];
				}
				// alert(header+","+content);
				var headers = header.split("][");
				cmd = headers[0];
				time = headers[1];
				sender = headers[2];
				
				if (cmd=="SYSTEM"){
					var online = headers[2];
					$("#onlinecount").html(""+online);
					CHAT.addSystemTip(content);
				} else if(cmd =="CHAT"){
					// 解析消息的发送时间
					var date = new Date(parseInt(time));
					// 添加一个系统时间标签
					CHAT.addSystemTip('<span class="time-label">'+ date.format("hh:mm:ss")+"</span>");
					// 把聊天内容 加到聊天面板中去
					var ismine= (send=="you")?true:false;
					var contentDiv = "<div>" + content +"</div>";
					var usernameDiv = "<span>"+ sender +"</span>";
					var section = document.createElement("section");
					// 用来区分聊天消息是自己发的，还是别人发的
			        // 显示出来的样式不一样而已
					
					if (ismine){
						section.className = "user";
						section.innerHTML = contentDiv + usernameDiv;
					} else {
						section.className = "service";
						section.innerHTML = usernameDiv + contentDiv;
					}
					 // 最后把创建好的元素添加到聊天面板
					$("#online-msg").append(section);
				} else if(cmd =="FLOWER"){
					// 先出现一个提示
					CHAT.addSystemTip(content);
					// 触发一个送鲜花的特效
					$(document).snowfall('clear');
					$(document).snowfall({
						image:"/images/face/50.gif",
						flakeCount:60,
						minSize:20,
						maxSize:40
					});
					window.flowerTimer = window.setTimeout(function(){
						$(document).snowfall('clear');
						window.clearTimeout(flowerTimer);
					},5000);
				}
				// 将屏幕滚动到最后
				CHAT.scrollToBottom();
				
			} ;
			// 判断浏览器是否支持WebSocket协议
			if (!window.WebSocket){
				CHAT.socket = window.MozWebSocket;
			}
			if (window.WebSocket){
				CHAT.socket = new WebSocket(CHAT.serverAddr);
				CHAT.socket.onmessage=function(e){
					console.log("获取服务器发来的消息" + e.data);
					appendToPanel(e.data);
				};
				CHAT.socket.onopen=function(e){
					console.log("与服务器建立连接");
					CHAT.socket.send("[LOGIN]["+new Date().getTime() +"][" + CHAT.nickname+"]");
				};
				CHAT.socket.onclose=function(e){
					console.log("服务器关闭");
				};
			} else {
				alert("你的浏览器不支持websocket!");
			}
		}
	}
	
})