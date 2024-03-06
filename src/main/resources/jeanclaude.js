
var markdownConverter = new showdown.Converter();

var setMarkdown = function(div, md){
	div.innerHTML = md;
//	div.innerHTML = md.replaceAll("\r", "").replaceAll("\n", "<br>")
//	div.innerHTML = markdownConverter.makeHtml(md);
//	div.querySelectorAll('pre').forEach((block) => {
//		block.innerHTML = "CODE";
//	});
//	for (var block in div.querySelectorAll('code')) {
//		block.innerHTML = "CODE";
//		//hljs.highlightBlock(block);
//	}
};
