exports.getConnection = function(){
	var mysql = require("mysql")
	var dbinfo = require("./dbinfo.json")

	dbinfo.multipleStatements = true

	var connection = mysql.createConnection(dbinfo)	
	
	return connection
}

