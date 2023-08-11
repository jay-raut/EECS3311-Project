*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds

Suite Setup    Create Session    localhost    http://localhost:8080
#the database needs to be reset and empty for the controlled test environment
#othervise we will get unintended 400 and 500 everywhere
*** Test Cases ***
#####ADD ACTORS#########################################################################################
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
#already added person, should fail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kavin Bacon   actorId=kavinbacon
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400
addActorFail1
    #this needs to fail cuz it has "unique" name but id exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=KevinBacon DifferentName   actorId=kavinbacon
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400
addActorFail2
    #I expect this to fail cuz of bad formattiong i wote actorID and NAME wrong
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    Actorid=robertdowney    Name=Robert Downey Jr.
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400
addActorFail3
    #I expect this to fail cuz of nulls
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=${null}   actorId=scarlettjohansson
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400
addActorFail4
    #I expect this to fail cuz of nulls
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Scarlett Johansson   actorId=${null}
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400


########ADD MOVIES##############################################################################################
addMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A Few Good Men   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200
addMoviePass1
    #pass cus has a diff id
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A Few Good Men   movieId=afewgoodmenuniqueanddifferentid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

     # not necessary but sets up some environment .... (empty for now)


addMovieFail
    #fail cuz i already added exact same thing
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
addMovieFail1
    #fail cuz movieId exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
addMovieFail2
    #fail cuz bad formatting (wrote parameters name wrong Movie id and Name)
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    MovieId=afewgoodmenid   Name=A_Few_Good_Men_butDifferentName
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
<<<<<<< Updated upstream
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
=======




addRelationshipPass
    #for unique actorID and movieID
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=afewgoodmenid  ActorId=kavinbacon
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

addRelationshipFail
    #for improper format // movieId is null
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=${null}  ActorId=kavinbacon
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=404

addRelationshipFail2
    #for improper format // actorId is null
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=afewgoodmenid  ActorId=${null}
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=404

addRelationshipFail3
    #for improper format // movieId is missingRequiredInfo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=afewgoodmenidMissing  ActorId=kavinbacon
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

addRelationshipFail4
    #for improper format // actorId is missingRequiredInfo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=afewgoodmenid ActorId=kavinbaconMissing
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

addRelationshipFail5
    #for improper format // actorId is missingRequiredInfo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=afewgoodmenid  ActorId=kavinbacon
    ${resp}=      PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

>>>>>>> Stashed changes
