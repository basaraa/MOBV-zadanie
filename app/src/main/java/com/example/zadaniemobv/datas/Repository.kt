package com.example.zadaniemobv.datas

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.zadaniemobv.database.DatabaseDAO
import com.example.zadaniemobv.database.fromDatabaseToNormalInstance
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.zadaniemobv.webservice.*
import java.io.IOException

class Repository (
    private val databaseDAO: DatabaseDAO,
    private val webService: WebService
){
    //zoznam podnikov aj s počtom používateľov
    suspend fun updatePubUsers(
        onError: (error: String) -> Unit,
        lat: Double?,
        lon: Double?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO) {
            try {
                val response = webService.findPubUsers()

                if (response.isSuccessful) {
                    response.body()?.let { pubList->
                        withContext(dispatcher) {
                            val databasepubUsersList = pubList.map {it.fromWebtoDatabaseInstance().apply {
                                distance =
                                    if (lat!=null && lon!=null) distanceTo(MyLocationCoordinates(lat,lon))
                                    else null
                            }}.filter { pub -> pub.name != "" }
                            databaseDAO.clearPubUsers()
                            databaseDAO.insertAllPubUsers(databasepubUsersList)
                        }
                    }
                }
                else {
                    onError("Zlyhanie načítania dát.")

                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                onError("Zlyhanie načítania dát, skontrolujte si internetové pripojenie")

            } catch (ex: Exception) {
                ex.printStackTrace()
                onError("Zlyhanie načítania dát, error.")
            }

    }
    fun getAllPubsByNameAsc() : LiveData<List<PubUser>>{
        return Transformations.map(databaseDAO.getAllPubsByNameAsc()){
            it.fromDatabaseToNormalInstance()
        }
    }
    fun getAllPubsByNameDesc() : LiveData<List<PubUser>>{
        return Transformations.map(databaseDAO.getAllPubsByNameDesc()){
            it.fromDatabaseToNormalInstance()
        }
    }
    fun getAllPubsByUsersAsc() : LiveData<List<PubUser>>{
        return Transformations.map(databaseDAO.getAllPubsByUsersAsc()){
            it.fromDatabaseToNormalInstance()
        }
    }
    fun getAllPubsByUsersDesc() : LiveData<List<PubUser>>{
        return Transformations.map(databaseDAO.getAllPubsByUsersDesc()){
            it.fromDatabaseToNormalInstance()
        }
    }
    fun getAllPubsByDistanceAsc() : LiveData<List<PubUser>>{
        return Transformations.map(databaseDAO.getAllPubsByDistanceAsc()){
            it.fromDatabaseToNormalInstance()
        }
    }
    fun getAllPubsByDistanceDesc() : LiveData<List<PubUser>>{
        return Transformations.map(databaseDAO.getAllPubsByDistanceDesc()){
            it.fromDatabaseToNormalInstance()
        }
    }

    //pub info
    suspend fun WebgetPub(userId: String, onError: (error:String)->Unit,dispatcher: CoroutineDispatcher = Dispatchers.IO): Pub? {
        var pubInfo:Pub?=null
        try {
            val query = "[out:json];node($userId);out body;>;out skel;"
            val response = webService.getPubDetail(query)
            if (response.isSuccessful) {
                response.body()?.let { allpubs->
                    if (allpubs.pubs.isNotEmpty()) {
                        withContext(dispatcher) {
                            pubInfo = allpubs.pubs[0].fromWebToNormalInstance()
                            val databasePub = databaseDAO.getPub(allpubs.pubs[0].fromWebToNormalInstance().id)
                            pubInfo!!.users= databasePub.users
                        }
                    }
                    else
                        onError ("Zlyhanie čítania dát")
                }
            }
            else {
                onError("Zlyhanie načítania dát.")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Zlyhanie načítania dát, skontrolujte si internetové pripojenie")

        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Zlyhanie načítania dát, error.")
        }

        return pubInfo

    }

    //blízke podniky
    suspend fun WebFindNearbyPubs(
        lat: Double, lon: Double,
        onError: (error: String) -> Unit,
    ) : List<Pub> {
        var pubList = listOf<Pub>()
        try {
            val q = "[out:json];node(around:250,$lat,$lon);(node(around:250)[\"amenity\"~\"^pub$|^bar$|^restaurant$|^cafe$|^fast_food$|^stripclub$|^nightclub$\"];);out body;>;out skel;"
            val resp = webService.findNearbyPubs(q)
            if (resp.isSuccessful) {
                resp.body()?.let { pubs ->
                    pubList = pubs.pubs.map { pubInfo->
                        pubInfo.fromWebToNormalInstance().apply {
                            distance = distanceTo(MyLocationCoordinates(lat,lon))
                        }
                    }
                    pubList = pubList.filter { it.name.isNotBlank() }.sortedBy { it.distance }
                } ?: onError("Zlyhanie načítania podnikov")
            } else {
                onError("Zlyhanie načítania podnikov")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Zlyhanie načítania podnikov, skontrolujte si internetové pripojenie")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Zlyhanie načítania podnikov, error.")
        }
        return pubList
    }

    //zapísanie sa do podniku
    suspend fun WebCheckIntoPub(
        pub: Pub,
        onError: (error: String) -> Unit,
        onSuccess: (success: Boolean) -> Unit,

    ) {
        try {
            val resp = webService.checkIntoPub(CheckIntoPubPostBody(pub.id,pub.name,pub.type,pub.lat.toDouble(),pub.lon.toDouble()))
            if (resp.isSuccessful) {
                resp.body()?.let {
                    onSuccess(true)
                }
            } else {
                onError("Zapísanie sa do podniku zlyhalo, skúste to neskôr prosím.")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Zapísanie sa do podniku zlyhalo, skontrolujte si internetové pripojenie)")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Zapísanie sa do podniku zlyhalo, error.")
        }
    }


    //registrácia
    suspend fun WebUserRegistration(
        name: String,
        password: String,
        onError: (error: String) -> Unit,
        onStatus: (success: WebUser?) -> Unit
    ) {
        try {
            val response = webService.registerUser(UserRegisterLoginPostBody(
                name = name,
                password = password)
            )
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    if (user.id == "-1"){
                        onStatus(null)
                        onError("Prihlasovacie meno už existuje, zadajte nejaké iné.")
                    }else {
                        onStatus(user)
                    }
                }
            } else {
                onError("Registrácia zlyhala, skúste to neskôr prosím.")
                onStatus(null)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Registrácia zlyhala, skontrolujte si internetové pripojenie")
            onStatus(null)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Registrácia zlyhala, error.")
            onStatus(null)
        }
    }

    //login
    suspend fun WebUserLogin(
        name: String,
        password: String,
        onError: (error: String) -> Unit,
        onStatus: (success: WebUser?) -> Unit
    ) {
        try {
            val response = webService.loginUser(UserRegisterLoginPostBody(name = name, password = password))
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    if (user.id == "-1"){
                        onStatus(null)
                        onError("Zlé zadané meno alebo heslo.")
                    }else {
                        onStatus(user)
                    }
                }
            } else {
                onError("Prihlásenie zlyhalo, skúste to neskôr prosím.")
                onStatus(null)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Prihlásenie zlyhalo, skontroluje si internetové pripojenie")
            onStatus(null)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Prihlásenie zlyhalo, error.")
            onStatus(null)
        }
    }


    //kamaráti
    suspend fun updateFriends(onError: (error: String) -> Unit,userId:String,dispatcher: CoroutineDispatcher = Dispatchers.IO) {

            try {
                val response = webService.findFriends()
                if (response.isSuccessful) {
                    response.body()?.let { friendList->
                        withContext(dispatcher) {
                        val databaseFriendList = friendList.map {it.fromWebtoDatabaseInstance(userId)}
                        databaseDAO.clearFriends(userId)
                        databaseDAO.insertAllFriends(databaseFriendList)
                        }
                    } ?: run {
                        onError("Nemáte žiadných kamarátov.")
                    }
                }
                else
                    onError("Zlyhanie načítania dát.")
            } catch (ex: IOException) {
                ex.printStackTrace()
                onError("Zlyhanie načítania dát, skontrolujte si internetové pripojenie")

            } catch (ex: Exception) {
                ex.printStackTrace()
                onError("Zlyhanie načítania dát, error.")
            }

    }
    fun getAllFriendsByNameAsc(id:String) : LiveData<List<Friend>>{
        return Transformations.map(databaseDAO.getAllFriendsByNameAsc(id)){
            it.fromDatabaseToNormalInstance()
        }
    }
    fun getAllFriendsByNameDesc(id:String) : LiveData<List<Friend>>{
        return Transformations.map(databaseDAO.getAllFriendsByNameDesc(id)){
            it.fromDatabaseToNormalInstance()
        }
    }
    //pridanie kamaráta
    suspend fun WebAddFriend(
        name: String,
        infoMessage: (message: String) -> Unit,
    ) {
        try {
            val response = webService.addFriend(AddFriendPostBody(contact = name))
            if (!(response.isSuccessful)) {
                infoMessage("Pridanie kamaráta zlyhalo, error")
            }
            else
                infoMessage("Úspešné pridanie kamaráta")
        }catch (ex: IOException) {
            ex.printStackTrace()
            infoMessage("Vymazanie kamaráta zlyhalo, skontroluje si internetové pripojenie")
        } catch (ex: Exception) {
            ex.printStackTrace()
            infoMessage("Vymazanie kamaráta zlyhalo, error.")
        }

    }
    //vymazanie kamaráta
    suspend fun WebDeleteFriend(
        name: String,
        infoMessage: (message: String) -> Unit,
    ) {
        try {
            val response = webService.deleteFriend(AddFriendPostBody(contact=name))
            if (response.isSuccessful) {
                infoMessage("Úspešné vymazanie kamaráta")
            }
            else
                infoMessage("Vymazanie kamaráta zlyhalo, error")
        } catch (ex: IOException) {
            ex.printStackTrace()
            infoMessage("Vymazanie kamaráta zlyhalo, skontroluje si internetové pripojenie")
        } catch (ex: Exception) {
            ex.printStackTrace()
            infoMessage("Vymazanie kamaráta zlyhalo, error.")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null
        fun getInstance(service: WebService, dao: DatabaseDAO): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: Repository(dao,service).also { INSTANCE = it }
            }
    }


}