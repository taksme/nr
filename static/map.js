$(function() {
	$("body").append("<section class='reports'><header>Reports</header><div class='content'></div></section>");
//	$("circle").attr("r", 1000);

	var ws = new WebSocket("ws://"+window.location.host);
	ws.onmessage = function (event) {
  		console.log(event.data);
  		var reports = JSON.parse(event.data);
  		for (var i=0; i<reports.length; i++) {
  			var r = reports[i];
	  		if (!r.plannedTs) continue;
	  		if (!r.loc) continue;
	  		if (r.evt!="ARRIVAL") continue;
	  		var minsLate = Math.floor((r.ts-r.plannedTs)/60000);
	  		var qsLate = Math.floor((r.ts-r.plannedTs)/15000)%4;
	  		var color = minsLate<=0 ? "ontime" : minsLate<5 ? "nearly" : "late";
	  		var point = $("circle#"+r.loc)
	  		if (!point.size()) continue;
	  		point.parent().append(point);
	  		console.log(color);
	  		var statuses = ["ontime","nearly","late","noreport"];
	  		point.attr("class", color); //add/removeClass don't work and we only ever store lateness in class
	  		var nextAttr = 16000;
	  		var cb = function() { nextAttr-=1000; point.attr("r", nextAttr); if (nextAttr>2000) setTimeout(cb, 50); }
	  		cb();
	  		var title = point.find("title").text();
	  		if (title) title = title.substring(0, title.indexOf("\n"));
	  		var time = ("00"+new Date(r.plannedTs).getHours()).substr(-2)+":"+("00"+new Date(r.plannedTs).getMinutes()).substr(-2);
	  		var dir = r.dir=="UP"? "⬆" : r.dir=="DOWN"? "⬇" : "";
	  		var evt = r.evt=="ARRIVAL" ? "ARR" : r.evt=="DEPARTURE" ? "DEP" : r.evt=="PASS" ? "PASS" : "-";
	  		var late = minsLate>0 || minsLate>0 && qsLate ? (""+minsLate+(qsLate==1 ? "¼" : qsLate==2 ? "½" : qsLate==3 ? "¾" : "")+"L") : "";
	  		point.find("title").text(title+"\n"+r.train.substr(2,4)+" "+time+" "+dir+ evt);
	  		$("section.reports div.content").prepend(
	  			"<div class='report "+color+"'>"+
	  				"<div class='headcode'>"+time+" "+dir+ evt+"</div>"+
	  				"<div class='punctuality'>"+late+"</div>"+
	  				"<div class='location'>"+title+
	  			"</div>");
	  	}
	}
	ws.onopen = function (event) {
  		ws.send("SUBSCRIBE REPORTS"); 
	};
});
