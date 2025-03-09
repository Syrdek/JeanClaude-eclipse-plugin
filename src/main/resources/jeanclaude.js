var jc = {
	markdownConverter: undefined,
	div: undefined,
	useHljs: false,
	messageId: 0,
	codeId: 0,

	useCopyBox: true,
	copyBoxMinLines: 3,

	history: [],

	init: function() {
		this.div = document.querySelector("#jeanclaude");

		this.markdownConverter = new showdown.Converter();
		this.markdownConverter.setOption("ellipsis", false);

		let userAgent = navigator.userAgent;
		if ((userAgent.indexOf('MSIE') >= 0 || userAgent.indexOf('Trident') >= 0) && userAgent.indexOf('Edge') < 0) {
			// User agent is IE
			this._setUseHljs(false);
		} else {
			this._setUseHljs(true);
		}
		
		this._addTextareaHook();

		this.clear();
		this._appendChat({ "role": "assistant", "content": "Hello, I'm Jean-Claude. Nice to eet you." });
	},

	clear: function() {
		while (this.div.firstChild) {
			this.div.removeChild(this.div.firstChild);
		}

		this.history = [];
	},

	setChat: function(messages) {
		let wasScrolledBotton = this._isScrolledToBottom();

		let existingNodes = this.div.querySelectorAll("div.jc-message");
		if (existingNodes.length > messages.length) {
			this.clear();
			existingNodes = [];
		}

		for (let i = 0; i < messages.length; i++) {
			let existingNode = existingNodes.length > i ? existingNodes[i] : null;
			if (!existingNode) {
				this._appendChat(messages[i]);
			} else {
				this._alterNode(existingNode, messages[i]);
			}
		}

		this.history = messages;

		if (wasScrolledBotton) {
			this._scrollToBottom();
		}
	},
	
	setError: function(message) {
		let wasScrolledBotton = this._isScrolledToBottom();

		this.history.push(message);

		let node = document.createElement("div");
		node.setAttribute("id", "jc-message-" + ++this.messageId);
		node.setAttribute("role", message["role"]);
		node.classList.add("jc-message");
		node.classList.add("jc-role-error");
		
		let contentNode = document.createElement("div");
		contentNode.classList.add("jc-content");
		contentNode.innerHTML = "<p>" + message + "</p>";
		node.appendChild(contentNode);
		
		this._addFoldBox(node);
		this.div.appendChild(node);

		if (wasScrolledBotton) {
			this._scrollToBottom();
		}
	},

	_appendChat: function(message) {
		let wasScrolledBotton = this._isScrolledToBottom();

		this.history.push(message);

		let node = this._createNode(message);
		this.div.appendChild(node);

		if (wasScrolledBotton) {
			this._scrollToBottom();
		}
	},

	_addTextareaHook: function() {
		document.querySelector("#jc-chat-area").addEventListener("keydown", function(e) {
			if (e.ctrlKey && e.key === 'Enter') {
				document.querySelector('#jc-tell-btn').click();
			}
		});
	},
	
	_isScrolledToBottom: function() {
		return Math.abs(this.div.scrollHeight - this.div.clientHeight - this.div.scrollTop) < 1
	},

	_scrollToBottom: function() {
		if (typeof this.div.scrollTo === 'function') {
			this.div.scrollTo(0, this.div.scrollHeight);
		}
	},

	_alterNode: function(node, message) {
		node.className = "";
		node.setAttribute("role", message["role"]);
		node.classList.add("jc-message");
		node.classList.add("jc-role-" + message["role"].toLowerCase());
		node.classList.remove("jc-waiting");

		let contentNode = node.querySelector(".jc-content");
		if (!contentNode) {
			contentNode = document.createElement("div");
			contentNode.classList.add("jc-content");
			node.appendChild(contentNode);
		}

		let content = message["content"];
		let lowerContent = content.toLowerCase()
		let start = lowerContent.substring(0, 11);
		if (start == "@@waiting@@") {
			content = "<p>" + content.substring(11) + "</p>";
			node.classList.add("jc-waiting");
		} else {
			content = this._escapeMarkdown(message["content"]);
		}

		contentNode.innerHTML = content;

		let codeNodes = node.querySelectorAll('code');
		for (let c = 0; c < codeNodes.length; c++) {
			let codeNode = codeNodes[c];
			codeNode.setAttribute("id", "jc-code-" + ++this.codeId);
			this._highlight(codeNode);
			this._addCopyBox(codeNode);
		}

		return node;
	},

	_createNode: function(message) {
		let node = document.createElement("div");
		node.setAttribute("id", "jc-message-" + ++this.messageId);
		this._alterNode(node, message);
		this._addFoldBox(node);
		return node;
	},

	_escapeMarkdown: function(message) {
		return this.markdownConverter.makeHtml(message);
	},

	_setUseHljs: function(use) {
		this.useHljs = use;
		if (!use) {
			this.div.innerHTML = "<div class=\"jc-message jc-role-warn\">This internal browser is not compatible with code highlighting. Try changing it by adding -Dorg.eclipse.swt.browser.DefaultType=edge (for windows) to eclipse.ini</div>"
		}
	},

	_highlight: function(node) {
		if (this.useHljs) {
			hljs.highlightElement(node);
		}
	},

	_addFoldBox: function(node) {
		let box = document.createElement("button");
		box.classList.add("jc-fold");
		box.classList.add("mat-btn");
		box.innerHTML = "<svg xmlns=\"http://www.w3.org/2000/svg\" enable-background=\"new 0 0 24 24\" height=\"24\" viewBox=\"0 0 24 24\" width=\"24\"><g><rect fill=\"none\" height=\"24\" width=\"24\"/><path d=\"M12,20c-4.41,0-8-3.59-8-8s3.59-8,8-8s8,3.59,8,8S16.41,20,12,20 M12,22c5.52,0,10-4.48,10-10c0-5.52-4.48-10-10-10 C6.48,2,2,6.48,2,12C2,17.52,6.48,22,12,22L12,22z M11,12l0,4h2l0-4h3l-4-4l-4,4H11z\"/></g></svg>"

		const nodeId = node.getAttribute("id");
		box.onclick = function() {
			console.log("Node", nodeId, "selector", document.querySelector("#" + nodeId));
			document.querySelector("#" + nodeId).classList.toggle("folded");
		}
		node.appendChild(box);
	},

	_addCopyBox: function(node) {
		if (this.useCopyBox) {
			let lines = (node.textContent.match(/\n/g) || []).length
			if (lines < this.copyBoxMinLines) {
				return;
			}

			let box = document.createElement("button");
			box.classList.add("jc-copy");
			box.classList.add("mat-btn");
			box.innerHTML = "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"24\" viewBox=\"0 0 24 24\" width=\"24\"><path d=\"M0 0h24v24H0z\" fill=\"none\"/><path d=\"M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z\"/></svg>"

			const nodeId = node.getAttribute("id");
			box.onclick = function() {
				plugin_copy(document.querySelector("#" + nodeId).textContent);
			}
			node.appendChild(box);
		}
	}
}