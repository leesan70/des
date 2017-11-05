var utils = require("./utils")

/**
 * POST 
 * facebook_id
 * facebook_token
 * push_key
 * name
 */
exports.login = function (request, response){
    var query = request.body     
    var connection = utils.getConnection()
	
	connection.connect(function (err){
		if (err){
			connection.end()
			return next(err, null)
        }
        
        var selectQuery = "SELECT * FROM `user` WHERE facebook_id = ?"
        connection.query(selectQuery, [query.facebook_id], function(err, result){
            if (err){                
                connection.end()
                return next(err, null)
            }

            if (result.length == 0){
                var data = {
                    'facebook_id' : query.facebook_id,
                    'push_key' : query.push_key,
                    'name' : query.name           
                }
                var insertQuery = "INSERT INTO `user` SET ?;"
                connection.query(insertQuery, data, function(err, result){
                    if (err){
                        console.log(err)
                        connection.end()
                        return next(err, null)
                    }

                    return response.json({
                        "code" : "00",
                        "data" : "",
                    })
                })
            } else {
                return response.json({
                    "code" : "00",
                    "data" : result[0]
                })
            }
        })
    })
}

/**
 * PUT
 * cuurent_loc
 */
exports.updateLocation = function(request, response){
    var query = request.body
    var connection = utils.getConnection()

    conncetion.connect(function(err){
        if (err){
            connection.end()
            return next(err, null)        
        }

        var updateQuery = "UPDATE `user` SET current_loc = ?;"
        connection.query(updateQuery, [query.current_loc], function(err, result){
            if (err){
                connection.end()
                return next(err, result)
            }
            
            return response.json({
                "code" : "00"
            })
        })
    })
}

