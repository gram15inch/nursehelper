package org.techtown.nursehelper

import android.content.Context
import android.util.Log
import androidx.core.graphics.toColorInt
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.techtown.nursehelper.Schedule.userPatient
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class DBC(val mainActivity: MainActivity) {
    val from = "2013-04-08 10:10:10"
    val rootPath = "http://10.0.2.2:8090/myapp/01_jsp/"
    companion object {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmm")
        val domFormat = SimpleDateFormat("yyyyMMdd")
    }




    fun connect(id:String,pw:String):String{
        var Html =""

        // jsp에 http연결
        var urlStr =rootPath+"verifyUser.jsp"
        var url = URL(urlStr)
        val httpClient = url.openConnection() as HttpURLConnection

        setCookieHeader(httpClient)
        httpClient.setRequestMethod("POST") // URL 요청에 대한 메소드 설정 : POST.
        httpClient.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
        httpClient.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8")
        var para = "id=$id&pw=$pw"
        var os = OutputStreamWriter(httpClient.outputStream)
        os.write(para)
        os.flush()


        //jsp 에서 html 받기
        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("tst","http ok")
            try {
                Html = readStream(httpClient.inputStream)

/*   --
                val reader =
                      BufferedReader(InputStreamReader(httpClient.getInputStream(), "UTF-8"))
                  var line: String?
                  var data = ""
                  while (reader.readLine().also { line = it } != null) {
                      data += line
                  }*/

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.d("tst","ERROR ${httpClient.responseCode}")
            println("ERROR ${httpClient.responseCode}")
        }
        getCookieHeader(httpClient)
        //html 에서 body 추출
        val body = Jsoup.parse(Html).text()
        Log.d("tst","connectOk : $body")


        return body
    }

    fun select(qry:String):String{
        var Html =""

        // jsp에 http연결
        var urlStr =rootPath+"verifySession.jsp"
        var url = URL(urlStr)
        val httpClient = url.openConnection() as HttpURLConnection

        setCookieHeader(httpClient)
        httpClient.setRequestMethod("POST") // URL 요청에 대한 메소드 설정 : POST.
        httpClient.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
        httpClient.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8")
        var para = "qry=$qry"
        var os = OutputStreamWriter(httpClient.outputStream)
        os.write(para)
        os.flush()


        //jsp 에서 html 받기
        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("tst","http ok")
            try {
                Html = readStream(httpClient.inputStream)

/*   --
                val reader =
                      BufferedReader(InputStreamReader(httpClient.getInputStream(), "UTF-8"))
                  var line: String?
                  var data = ""
                  while (reader.readLine().also { line = it } != null) {
                      data += line
                  }*/

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.d("tst","ERROR ${httpClient.responseCode}")
            println("ERROR ${httpClient.responseCode}")
        }
        getCookieHeader(httpClient)
        //html 에서 body 추출
        val body = Jsoup.parse(Html).text()
        Log.d("tst","connectOk : $body")

        return body
    }

    //일정
    fun getSchedule(id:String, date:String):List<userSchedule>{
        var users = mutableListOf<userSchedule>()
        var Html =""

        // jsp에 http연결
        var urlStr =rootPath+"searchSchedule.jsp"
        var url = URL(urlStr)
        val httpClient = url.openConnection() as HttpURLConnection

        //setCookieHeader(httpClient)
        httpClient.setRequestMethod("POST") // URL 요청에 대한 메소드 설정 : POST.
        httpClient.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
        httpClient.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8")
        var para = "qry=ss&date=$date&id=$id"
        var os = OutputStreamWriter(httpClient.outputStream)
        os.write(para)
        os.flush()


        //jsp 에서 html 받기
        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("tst","http ok")
            try {
                Html = readStream(httpClient.inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.d("tst","ERROR ${httpClient.responseCode}")
            println("ERROR ${httpClient.responseCode}")
        }
        //getCookieHeader(httpClient)
        //html 에서 body 추출
        val body = Jsoup.parse(Html).text()

            if(body =="error"){
                val user = userSchedule(
                    -1,
                    "",
                    "",
                    dateFormat.parse("202101010101"),
                    dateFormat.parse("202101010101"),
            "",
            dateFormat.parse("202101010101"),
            0,
                )
                users.add(user)
                Log.d("tst","schedule error")
                return users
        }else {
            //json 에서 user 객체로 파싱
            val json = JSONObject(body)
            val jom = json.getString("main")
            val jarray = JSONArray(jom)
            for (i in 0..(jarray.length() - 1)) {
                val jo = jarray.getJSONObject(i)
                val user = userSchedule(
                    jo.getString("idcode").toInt(),
                    jo.getString("name"),
                    jo.getString("addr"),
                    dateFormat.parse(jo.getString("sdate")),
                    dateFormat.parse(jo.getString("edate")),
                    jo.getString("sex"),
                    domFormat.parse(jo.getString("dom")),
                    jo.getString("color").toColorInt(),
                )
                users.add(user)

            }


        return users
        }
    }
    fun deleteSchedule(id:String, sno:String):Int{

        var Html =""

        // jsp에 http연결
        var urlStr =rootPath+"deleteSchedule.jsp"
        var url = URL(urlStr)
        val httpClient = url.openConnection() as HttpURLConnection

        //setCookieHeader(httpClient)
        httpClient.setRequestMethod("POST") // URL 요청에 대한 메소드 설정 : POST.
        httpClient.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
        httpClient.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8")
        var para = "id=$id&sno=$sno"
        var os = OutputStreamWriter(httpClient.outputStream)
        os.write(para)
        os.flush()


        //jsp 에서 html 받기
        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("tst","http ok")
            try {
                Html = readStream(httpClient.inputStream)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.d("tst","ERROR ${httpClient.responseCode}")
            println("ERROR ${httpClient.responseCode}")
        }
        //getCookieHeader(httpClient)
        //html 에서 body 추출
        val body = Jsoup.parse(Html).text()


        when(body){
            "성공"-> return 1
            "실패"-> return -1
            else -> return 0
        }
    }
    fun inUpdateSchedule( id :String, sno :String, pno:String, sdate:String, edate:String, color:String):Int{

        return 1
    }

    //문서
    fun getDocument(id:String,pcode:String,name:String,addr:String,date:String):List<userDocument>{
        var users = mutableListOf<userDocument>()
        var Html =""
        // jsp에 http연결
        var urlStr =rootPath+"searchDocument.jsp"
        var url = URL(urlStr)
        val httpClient = url.openConnection() as HttpURLConnection
        //setCookieHeader(httpClient)
        httpClient.setRequestMethod("POST") // URL 요청에 대한 메소드 설정 : POST.
        httpClient.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
        httpClient.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8")
        var para = "qry=ss&id=$id&pcode=$pcode&name=$name&addr=$addr&date=$date"
        var os = OutputStreamWriter(httpClient.outputStream)
        os.write(para)
        os.flush()


        //jsp 에서 html 받기
        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("tst","http ok")
            try {
                Html = readStream(httpClient.inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.d("tst","ERROR ${httpClient.responseCode}")
            println("ERROR ${httpClient.responseCode}")
        }
        //getCookieHeader(httpClient)
        //html 에서 body 추출
        val body = Jsoup.parse(Html).text()
        if(body =="error"){
            val user = userDocument(
                -1,
                -1,
                "",
                "",
                dateFormat.parse("202101010101"),
                "",
                domFormat.parse("20210101"),
                ""
            )
            users.add(user)
            Log.d("tst","document error")
            return users
        }else {
            //json 에서 user 객체로 파싱
            val json = JSONObject(body)
            val jom = json.getString("main")
            val jarray = JSONArray(jom)
            for (i in 0..(jarray.length() - 1)) {
                val jo = jarray.getJSONObject(i)
                Log.d("tst","user[$i] :${jo}")
                var user =userDocument(
                    jo.getString("dcode").toInt(),
                    jo.getString("pcode").toInt(),
                    jo.getString("name"),
                    jo.getString("addr"),
                    domFormat.parse(jo.getString("date")),
                    jo.getString("sex"),
                    domFormat.parse(jo.getString("dom")),
                    jo.getString("memo"),
                )
                users.add(user)
            }

            return users
        }
    }
    fun inUpdateDocument(id:String,type:String,pcode: String,date: String,memo:String):Int{
        var users = mutableListOf<userDocument>()
        var Html =""
        // jsp에 http연결
        var urlStr =rootPath+"inUpDocument.jsp"
        var url = URL(urlStr)
        val httpClient = url.openConnection() as HttpURLConnection

        //setCookieHeader(httpClient)
        httpClient.setRequestMethod("POST") // URL 요청에 대한 메소드 설정 : POST.
        httpClient.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
        httpClient.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8")
        var para = "qry=ss&id=$id&type=$type&pcode=$pcode&date=$date&memo=$memo"
        var os = OutputStreamWriter(httpClient.outputStream)
        os.write(para)
        os.flush()


        //jsp 에서 html 받기
        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("tst","http ok")
            try {
                Html = readStream(httpClient.inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.d("tst","ERROR ${httpClient.responseCode}")
            println("ERROR ${httpClient.responseCode}")
        }
        //getCookieHeader(httpClient)

        //html 에서 body 추출
        val body = Jsoup.parse(Html).text()
        when(body){
            "success"-> return 1
            "db drror"-> return -2
            else -> return -3
        }
    }

    //환자
    fun getPatient(id:String,name:String):List<userPatient>{

        var users = mutableListOf<userPatient>()
        var Html =""
        // jsp에 http연결
        var urlStr =rootPath+"searchPatient.jsp"
        var url = URL(urlStr)
        val httpClient = url.openConnection() as HttpURLConnection
        //setCookieHeader(httpClient)
        httpClient.setRequestMethod("POST") // URL 요청에 대한 메소드 설정 : POST.
        httpClient.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
        httpClient.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8")
        var para = "qry=ss&id=$id&name=$name"
        var os = OutputStreamWriter(httpClient.outputStream)
        os.write(para)
        os.flush()

        //jsp 에서 html 받기
        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("tst","http ok")
            try {
                Html = readStream(httpClient.inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.d("tst","ERROR ${httpClient.responseCode}")
            println("ERROR ${httpClient.responseCode}")
        }
        //getCookieHeader(httpClient)
        //html 에서 body 추출
        val body = Jsoup.parse(Html).text()
        if(body =="error"){
            Log.d("tst","patient error")
            var user =userPatient(
                123,
                "name",
                "m",
                Calendar.getInstance().time,
                "addr1"
            )

            return users
        }else {
            //json 에서 user 객체로 파싱
            val json = JSONObject(body)
            val jom = json.getString("main")
            val jarray = JSONArray(jom)
            for (i in 0..(jarray.length() - 1)) {
                val jo = jarray.getJSONObject(i)
                Log.d("tst","user[$i] :${jo}")
                var user =userPatient(
                    jo.getString("pcode").toInt(),
                    jo.getString("name"),
                    jo.getString("sex"),
                    domFormat.parse(jo.getString("dom")),
                    jo.getString("addr")
                )
                users.add(user)
            }

            return users
        }
    }


    //사용자 계정
    fun login(id:String, pw:String):Int {
        var Html =""

        // jsp에 http연결
        var urlStr =rootPath+"mainLogin.jsp"
        Log.d("tst",urlStr)
        var url = URL(urlStr)
        val httpClient = url.openConnection() as HttpURLConnection

        //setCookieHeader(httpClient)
        httpClient.setRequestMethod("POST") // URL 요청에 대한 메소드 설정 : POST.
        httpClient.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
        httpClient.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8")
        var para = "id=$id&pw=$pw"
        var os = OutputStreamWriter(httpClient.outputStream)
        os.write(para)
        os.flush()


        //jsp 에서 html 받기
        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("tst","http ok")
            try {
                Html = readStream(httpClient.inputStream)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.d("tst","ERROR ${httpClient.responseCode}")
            println("ERROR ${httpClient.responseCode}")
        }
        //getCookieHeader(httpClient)

        //html 에서 body 추출
        val body = Jsoup.parse(Html).text()
        Log.d("tst","connectOk : $body")


        when(body){
            "성공"-> return 1
            "실패"-> return 0
            else -> return -1
        }


    }
    fun regist(id:String, pw:String, name:String, sex: String, pn:String):Int{
        //1성공 0실패 -2 db오류

        var Html =""

        // jsp에 http연결
        var urlStr =rootPath+"createUser.jsp"
        var url = URL(urlStr)
        val httpClient = url.openConnection() as HttpURLConnection

        //setCookieHeader(httpClient)
        httpClient.setRequestMethod("POST") // URL 요청에 대한 메소드 설정 : POST.
        httpClient.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
        httpClient.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8")
        var para = "id=$id&pw=$pw&name=$name&sex=$sex&pn=$pn"
        var os = OutputStreamWriter(httpClient.outputStream)
        os.write(para)
        os.flush()


        //jsp 에서 html 받기
        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("tst","http ok")
            try {
                Html = readStream(httpClient.inputStream)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.d("tst","ERROR ${httpClient.responseCode}")
            println("ERROR ${httpClient.responseCode}")
        }
        //getCookieHeader(httpClient)
        //html 에서 body 추출
        val body = Jsoup.parse(Html).text()
        Log.d("tst","connectOk : $body")


        when(body){
            "성공"-> return 1
            "계정"-> return -1
            "db"-> return -1
            else -> return 0
        }

    }
    fun idCheck(id:String):Int{
        var Html =""

        // jsp에 http연결
        var urlStr =rootPath+"idCheck.jsp"
        var url = URL(urlStr)
        val httpClient = url.openConnection() as HttpURLConnection

        setCookieHeader(httpClient)
        httpClient.setRequestMethod("POST") // URL 요청에 대한 메소드 설정 : POST.
        httpClient.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
        httpClient.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8")
        var para = "cmd=idCheck&id=$id"
        var os = OutputStreamWriter(httpClient.outputStream)
        os.write(para)
        os.flush()


        //jsp 에서 html 받기
        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("tst","http ok")
            try {
                Html = readStream(httpClient.inputStream)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.d("tst","ERROR ${httpClient.responseCode}")
            println("ERROR ${httpClient.responseCode}")
        }
        getCookieHeader(httpClient)
        //html 에서 body 추출
        val body = Jsoup.parse(Html).text()
        Log.d("tst","connectOk : $body")

        when(body){
            "중복"-> return 1
            "id없음"-> return 0
            else -> return -1
        }


    }

    fun readStream(inputStream: InputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }
    fun setCookieHeader(con:HttpURLConnection){
        var pref = mainActivity.getSharedPreferences("sessionCookie", Context.MODE_PRIVATE);
        var sessionid = pref.getString("sessionid",null)
        if(sessionid!=null) {
            Log.d("tst","세션 아이디"+sessionid+"가 요청 헤더에 포함 되었습니다.")
            con.setRequestProperty("Cookie", sessionid)
        }
    }

    fun getCookieHeader(con:HttpURLConnection){//Set-Cookie에 배열로 돼있는 쿠키들을 스트링 한줄로 변환
        var cookies = con.getHeaderFields().get("Set-Cookie")
        //cookies -> [JSESSIONID=D3F829CE262BC65853F851F6549C7F3E; Path=/smartudy; HttpOnly] -> []가 쿠키1개임.
        //Path -> 쿠키가 유효한 경로 ,/smartudy의 하위 경로에 위의 쿠키를 사용 가능.
        if (cookies != null) {
            for (cookie in cookies) {
                var sessionid = cookie.split(";\\s*")[0]
                //JSESSIONID=FB42C80FC3428ABBEF185C24DBBF6C40를 얻음.
                //세션아이디가 포함된 쿠키를 얻었음.
                setSessionIdInSharedPref(sessionid)
            }
        }
    }

    fun setSessionIdInSharedPref(sessionid:String){
        var pref = mainActivity.getSharedPreferences("sessionCookie", Context.MODE_PRIVATE)
        var edit = pref.edit()
        if(pref.getString("sessionid",null) == null){ //처음 로그인하여 세션아이디를 받은 경우
            Log.d("tst","처음 로그인하여 세션 아이디를 pref에 넣었습니다."+sessionid)
        }else if(!pref.getString("sessionid",null).equals(sessionid)){ //서버의 세션 아이디 만료 후 갱신된 아이디가 수신된경우
            Log.d("tst","기존의 세션 아이디"+pref.getString("sessionid",null)+"가 만료 되어서 "
                    +"서버의 세션 아이디 "+sessionid+" 로 교체 되었습니다.")
        }
        edit.putString("sessionid",sessionid)
        edit.apply() //비동기 처리
    }



}