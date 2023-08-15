*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds

Suite Setup    Create Session    localhost    http://localhost:8080
#the database needs to be reset and empty for the controlled test environment
#othervise we will get unintended 400 and 500 everywhere
*** Test Cases ***

addActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kevin Bacon   actorId=nm0000102
    
#this is what i planned to do but prof said he would make sure to add kevinbacon before runnin our tests I will put it here anyway just to be safe

#    ${headers}=    Create Dictionary    Content-Type=application/json
#    ${params}=    Create Dictionary    name=Kevin Bacon   actorId=nm0000102
#    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
#    # unique id, altho actor exists it has diff name
#    ${headers}=    Create Dictionary    Content-Type=application/json
#    ${params}=    Create Dictionary    name=Kevin Bacon   actorId=nm0000102butuniqueid
#    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=ActorName   actorId=000ID
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
    # unique id, altho actor name exists it has diff id
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=ActorName   actorId=000IDbutuniqueid
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200


addActorFail
    #this needs to fail cuz it has "unique" name but id exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kevin Bacon2   actorId=nm0000102
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

    #I expect this to fail cuz of bad formattiong i wote actorID and NAME wrong
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    Actorid=robertdowney    Name=Robert Downey Jr.
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

    #I expect this to fail cuz of nulls
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=${null}   actorId=scarlettjohansson
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

    #I expect this to fail cuz of nulls
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Scarlett Johansson   actorId=${null}
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

addMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A Few Good Men   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

    #pass cus has a diff id
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A Few Good Men   movieId=afewgoodmenuniqueanddifferentid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

addMovieFail
    #fail cuz movieId exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

    #fail cuz bad formatting
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=afewgoodmenid   name=A_Few_Good_Men_butDifferentName
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

    #fail cuz Nulls
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

    #fail cuz movieId exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

    #fail cuz Nulls
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=${null}
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

    #fail cuz Nulls again
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=${null}   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400


######ADD RLATIONSHIPS#########################################################################################

addRelationshipPass
    #for unique actorID and movieID
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102   movieId=afewgoodmenid
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200


addRelationshipFail
    #same relationship exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102   movieId=afewgoodmenid
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

    #for improper format // movieId is null
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=nm0000102   movieId=${null}
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

    #for improper format // actorId is null
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=${null}   movieId=afewgoodmenid
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

    #for improper format // movieId is missingRequiredInfo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=nm0000102   movieId=nonexistentID
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=404

    #for improper format // actorId is missingRequiredInfo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=nonexistingActorID   movieId=afewgoodmenid
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=404

    #relationship already exixts
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=nm0000102   movieId=afewgoodmenid
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400
#######GET ACTOR#####################################################################
getActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/getActor    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    Should Be Equal As Strings    ${resp.json()['actorId']}    nm0000102
    Should Be Equal As Strings    ${resp.json()['name']}    Kevin Bacon
    List Should Contain Value    ${resp.json()['movies']}     afewgoodmenid

getActorFail
    #fail cuz bad formatting
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     Nonexistentparameter=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/getActor    params=${params}    headers=${headers}    expected_status=400

    #fail cuz nullz
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=${null}
    ${resp}=    GET On Session    localhost    /api/v1/getActor    params=${params}    headers=${headers}    expected_status=400

    #fail cuz not found
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=nonexistentId
    ${resp}=    GET On Session    localhost    /api/v1/getActor    params=${params}    headers=${headers}    expected_status=404

#######GET MOVIE#############################################################################
getMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=afewgoodmenid
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    Should Be Equal As Strings    ${resp.json()['movieId']}    afewgoodmenid
    Should Be Equal As Strings    ${resp.json()['name']}    A Few Good Men
    List Should Contain Value    ${resp.json()['actors']}     nm0000102

getMovieFail
    #fail cuz bad formatting
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     NotARealParameter=afewgoodmenid
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    params=${params}    headers=${headers}    expected_status=400

    #fail cuz Nulz
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=${null}
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    params=${params}    headers=${headers}    expected_status=400

    #fail cuz not found
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=NotARealMovieID
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    params=${params}    headers=${headers}    expected_status=404


#######GET RELATIONSHIP#############################################################################
getRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=afewgoodmenid    actorId=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    Should Be Equal As Strings    ${resp.json()['movieId']}    afewgoodmenid
    Should Be Equal As Strings    ${resp.json()['actorId']}    nm0000102
    Should Be True    ${resp.json()['hasRelationship']}

getRelationshipFail
    #fail cuz bad formatting // nonexistent parameter for movieId
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     Nonexistentparameter=afewgoodmenid         actorId=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=400

    #fail cuz bad formatting // nonexistent parameter for actorId
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=afewgoodmenid         Nonexistentparameter=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=400

    #fail cuz movieId is null
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=${null}         actorId=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=400

    #fail cuz actorId is null
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=afewgoodmenid         actorId=${null}
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=400

    #fail cuz not found
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=nonexistentId         actorId=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=404

    #fail cuz not found
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=afewgoodmenid         actorId=nonexistentId
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=404

####### GET BACON NUMBER #################################################################################################
BaconNumberPreWork
# Add Actors
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     name=Johnny Depp   actorId=johnnydepp
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Angelina Jolie   actorId=angelinajolie
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Actor B   actorId=actorb
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Actor OUT OF REACH   actorId=outOfReachID
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

# Addmovies
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Movie 1   movieId=movie1id
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Movie 2   movieId=movie2id
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Movie 3   movieId=movie3id
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=UnBoundedMovie   movieId=unboundMovieID
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

#######addrelationships
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=angelinajolie   movieId=movie1id
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=angelinajolie   movieId=afewgoodmenid
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=actorb   movieId=movie2id
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=actorb   movieId=movie1id
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=johnnydepp   movieId=movie2id
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=outOfReachID   movieId=unboundMovieID
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

computeBaconNumberPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    Should Be Equal As Integers    ${resp.json()['baconNumber']}    0

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=angelinajolie
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    Should Be Equal As Integers    ${resp.json()['baconNumber']}    1

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=actorb
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    Should Be Equal As Integers    ${resp.json()['baconNumber']}    2

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=johnnydepp
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    Should Be Equal As Integers    ${resp.json()['baconNumber']}    3

computeBaconNumberFail
    #fail cuz actorID not found
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=RandomIdDoesntExist
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    params=${params}    headers=${headers}    expected_status=404

    #fail cuz actor is either unreachable or path is more than 6 (unreachable for my test)
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=outOfReachID
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    params=${params}    headers=${headers}    expected_status=404

    #fail cuz actor nullz
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=${null}
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    params=${params}    headers=${headers}    expected_status=400

    #fail cuz bad formatting
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     wrongParameter=someid
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    params=${params}    headers=${headers}    expected_status=400

###############BACON PATH TESTS###############################
computeBaconPathPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    ${returnedPath}=    Set Variable    ${resp.json()["baconPath"]}
    ${expectedPath}=    Create List    nm0000102
    List Should Contain Sub List    ${returnedPath}    ${expectedPath}

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=angelinajolie
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    ${returnedPath}=    Set Variable    ${resp.json()["baconPath"]}
    ${expected_path}=    Create List    angelinajolie     	afewgoodmenid      nm0000102
    List Should Contain Sub List    ${returnedPath}    ${expected_path}

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=actorb
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    ${returnedPath}=    Set Variable    ${resp.json()["baconPath"]}
    ${expected_path}=    Create List    actorb       movie1id        angelinajolie     	afewgoodmenid      nm0000102
    List Should Contain Sub List    ${returnedPath}    ${expected_path}

    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=johnnydepp
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    ${returnedPath}=    Set Variable    ${resp.json()["baconPath"]}
    ${expected_path}=    Create List    	johnnydepp      	movie2id        actorb       movie1id        angelinajolie     	afewgoodmenid      nm0000102
    List Should Contain Sub List    ${returnedPath}    ${expected_path}

computeBaconPathFail
    #wrong/bad formatting
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     wrongParameter=someid
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath   params=${params}    headers=${headers}    expected_status=400

    #missing information
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=${null}
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath   params=${params}    headers=${headers}    expected_status=400

    #fail cuz actorID not found
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=RandomIdDoesntExist
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    params=${params}    headers=${headers}    expected_status=404

    #fail cuz actor is either unreachable or path is more than 6 (unreachable for my test)
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=outOfReachID
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    params=${params}    headers=${headers}    expected_status=404


###############EXTENDED OPTION (DELETE) TESTS###############################
deleteActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=angelinajolie
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteActor    params=${params}    headers=${headers}    expected_status=200

deleteActorFail
    #fail cuz badformatting
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    badparameter=johnnydepp
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteActor    params=${params}    headers=${headers}    expected_status=400

    #fail cuz actor doesnt exist anymore (deleted already)
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=angelinajolie
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteActor    params=${params}    headers=${headers}    expected_status=404

    #fail cuz nulz
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=${null}
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteActor    params=${params}    headers=${headers}    expected_status=400

deleteMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=movie3id
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteMovie    params=${params}    headers=${headers}    expected_status=200

deleteMovieFail
    #fail cuz badformatting
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    badParameter=afewgoodmenuniqueanddifferentid
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteMovie    params=${params}    headers=${headers}    expected_status=400

    #fail cuz movie not found
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    badParameter=noMovieFound
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteMovie    params=${params}    headers=${headers}    expected_status=400

    #fail cuz nulz
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    badParameter=${null}
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteMovie    params=${params}    headers=${headers}    expected_status=400

deleteRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=outOfReachID     movieId=unboundMovieID
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteRelationship    params=${params}    headers=${headers}    expected_status=200

deleteRelationshipFail
    #fail cuz bad formatting
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    badparam=nm0000102       movieId=afewgoodmenid
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteRelationship    params=${params}    headers=${headers}    expected_status=400

    #fail cuz nulz
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=${null}      movieId=unboundMovieID
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteRelationship    params=${params}    headers=${headers}    expected_status=400

    #fail cuz nulz
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=outOfReachID     movieId=${null}
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteRelationship    params=${params}    headers=${headers}    expected_status=400

    #fail cuz notfound
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=actornotreal     movieId=unboundMovieID
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteRelationship    params=${params}    headers=${headers}    expected_status=404

    #fail cuz notfound2
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=outOfReachID     movieId=movienotreal
    ${resp}=    DELETE On Session    localhost    /api/v1/deleteRelationship    params=${params}    headers=${headers}    expected_status=404
