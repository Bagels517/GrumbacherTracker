<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>

	<head>
		<title>GSFC Populations</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/index_css.css" />
		
	</head>
	
	<body>
		<h1>Populations</h1>
		
		<form action="${pageContext.servletContext.contextPath}/index" method="post">
			<table align="center">
			
			 <%-- test button --%>
				<tr>
					<td><input type="Submit" name="Test Button" value="Test Button"></td>
				
				</tr>
				
				
				
				<tr>
					<td><input type="Submit" name="TotalPopulation" value="Total Population"></td>
					<td>${model.popTotal}</td>
					<td><div class="myProgress"><div id="totalBar"><p id="percent"></p></div></div></td>
					
					<script>
						var totalBar = ((${model.popTotal} / 400) * 100);
						var totalBar2 = totalBar.toFixed(0);
						document.getElementById("totalBar").style.width = totalBar + "%";
						document.getElementById("percent").innerHTML = totalBar2 + "%";
					</script>
				</tr>
				
				<tr>
					<td><input type="Submit" name="WeightRoom" value="Weight Room"></td>
					<td>${model.weightRoom}</td>
					<td><div class="myProgress"><div id="weightBar"><p id="percent2"></p></div></div></td>
					
					<script>
						var weightBar = ((${model.weightRoom} / 75) * 100);
						var weightBar2 = weightBar.toFixed(0);
						document.getElementById("weightBar").style.width = weightBar + "%";
						document.getElementById("percent2").innerHTML = weightBar2 + "%";
					</script>
				</tr>
				
				<tr>
					<td><input type="Submit" name="RacquetBall" value="Racquet Ball"></td>
					<td>${model.racquet}</td>
					<td><div class="myProgress"><div id="racquetBar"><p id="percent3"></p></div></div></td>
					
					<script>
						var racquetBar = ((${model.racquet} / 6) * 100);
						var racquetBar2 = racquetBar.toFixed(0);
						document.getElementById("racquetBar").style.width = racquetBar + "%";
						document.getElementById("percent3").innerHTML = racquetBar2 + "%";
					</script>
				</tr>
				
				<tr>
					<td><input type="Submit" name="Pool" value="Pool"></td>
					<td>${model.pool}</td>
					<td><div class="myProgress"><div id="poolBar"><p id="percent4"></p></div></div></td>
					
					<script>
						var poolBar = ((${model.pool} / 75) * 100);
						var poolBar2 = poolBar.toFixed(0);
						document.getElementById("poolBar").style.width = poolBar + "%";
						document.getElementById("percent4").innerHTML = poolBar2 + "%";
					</script>
				</tr>
				
				<tr>
					<td><input type="Submit" name="FieldHouse" value="Field House"></td>
					<td>${model.fieldHouse}</td>
					<td><div class="myProgress"><div id="fieldBar"><p id="percent5"></p></div></div></td>
					
					<script>
						var fieldBar = ((${model.fieldHouse} / 200) * 100);
						var fieldBar2 = fieldBar.toFixed(0);
						document.getElementById("fieldBar").style.width = fieldBar + "%";
						document.getElementById("percent5").innerHTML = fieldBar2 + "%";
					</script>
				</tr>
				
				<tr>
					<td><input type="Submit" name="WolfBasketball" value="Wolf Basketball"></td>
					<td>${model.wolfBasketball}</td>
					<td><div class="myProgress"><div id="wolfBar"><p id="percent6"></p></div></div></td>
					
					<script>
						var wolfBar = ((${model.wolfBasketball} / 200) * 100);
						var wolfBar2 = wolfBar.toFixed(0);
						document.getElementById("wolfBar").style.width = wolfBar + "%";
						document.getElementById("percent6").innerHTML = wolfBar2 + "%";
					</script>
				</tr>
				
				<tr>
					<td><input type="Submit" name="RockWall" value="Rock Wall"></td>
					<td>${model.rockWall}</td>
					<td><div class="myProgress"><div id="rockBar"><p id="percent7"></p></div></div></td>
					
					<script>
						var rockBar = ((${model.rockWall} / 10) * 100);
						var rockBar2 = rockBar.toFixed(0);
						document.getElementById("rockBar").style.width = rockBar + "%";
						document.getElementById("percent7").innerHTML = rockBar2 + "%";
					</script>
				</tr>
				
				<tr>
					<td><input type="Submit" name="DanceRoom" value="Dance Room"></td>
					<td>${model.danceRoom}</td>
					<td><div class="myProgress"><div id="danceBar"><p id="percent8"></p></div></div></td>
					
					<script>
						var danceBar = ((${model.danceRoom} / 30) * 100);
						var danceBar2 = danceBar.toFixed(0);
						document.getElementById("danceBar").style.width = danceBar + "%";
						document.getElementById("percent8").innerHTML = danceBar2 + "%";
					</script>
				</tr>
			</table>
			
		</form>
		
	</body>
</html>
