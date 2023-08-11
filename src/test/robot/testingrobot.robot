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
    ${params}=    Create Dictionary    name=Kavin Bacon   actorId=kavinbacon
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
# unique id, altho actor exists it has diff name
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kavin Bacon   actorId=kavinbaconbutuniqueid
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
# not necessary but sets up some environment ...


addActorFail
    #this needs to fail cuz it has "unique" name but id exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kavin Bacon2   actorId=kavinbacon
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

     # not necessary but sets up some environment ....
addMovieFail
    #fail cuz movieId exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
addMovieFail2
    #fail cuz bad formatting
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=afewgoodmenid   name=A_Few_Good_Men_butDifferentName
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

addMovieFail3
    #fail cuz Nulls
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
addMovieFail4
    #fail cuz movieId exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

addMovieFail5
    #fail cuz Nulls
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=${null}
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
addMovieFail6
    #fail cuz Nulls again
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=${null}   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400


######ADD RLATIONSHIPS#########################################################################################

addRelationshipPass
    #for unique actorID and movieID
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=kavinbacon   movieId=afewgoodmenid
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200
#######not necessary for test but for set up later tests (empty for now)...


addRelationshipFail
    #for improper format // movieId is null
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=kavinbacon   movieId=${null}
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

addRelationshipFail2
    #for improper format // actorId is null
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=${null}   movieId=afewgoodmenid
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

addRelationshipFail3
    #for improper format // movieId is missingRequiredInfo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=kavinbacon   movieId=nonexistentID
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=404

addRelationshipFail4
    #for improper format // actorId is missingRequiredInfo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=nonexistingActorID   movieId=afewgoodmenid
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=404

addRelationshipFail5
    #relationship already exixts
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=kavinbacon   movieId=afewgoodmenid
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400
#######GET ACTOR#####################################################################
getActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     actorId=kavinbacon
    ${resp}=    GET On Session    localhost    /api/v1/getActor    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    Should Be Equal As Strings    ${resp.json()['actorId']}    kavinbacon
    Should Be Equal As Strings    ${resp.json()['name']}    Kavin Bacon
    List Should Contain Value    ${resp.json()['movies']}     afewgoodmenid

#######GET MOVIE#############################################################################
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=afewgoodmenid
    ${resp}=    GET On Session    localhost    /api/v1/getActor    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    Should Be Equal As Strings    ${resp.json()['movieId']}    afewgoodmenid
    Should Be Equal As Strings    ${resp.json()['name']}    A Few Good Men
    List Should Contain Value    ${resp.json()['actors']}     kavinbacon


#######GET RELATIONSHIP#############################################################################
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     movieId=afewgoodmenid      actorId=kavinbacon

    ${resp}=    GET On Session    localhost    /api/v1/getActor    params=${params}    headers=${headers}    expected_status=200
    #check if content of response is correct
    Should Be Equal As Strings    ${resp.json()['movieId']}    afewgoodmenid
    Should Be Equal As Strings    ${resp.json()['name']}    A Few Good Men
    List Should Contain Value    ${resp.json()['actors']}     kavinbacon

