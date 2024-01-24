package bangkit.roy.storyappmaster.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import bangkit.roy.storyappmaster.R
import bangkit.roy.storyappmaster.api.RetrofitClient
import bangkit.roy.storyappmaster.databinding.ActivityRegistrasiBinding
import bangkit.roy.storyappmaster.model.RegisResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Registrasi : AppCompatActivity(),View.OnClickListener {

    private lateinit var btnIntent : Button
    private lateinit var registrasiBinding: ActivityRegistrasiBinding

    companion object {
        private const val EXTRA_REGIS = "Registrasi"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrasiBinding = ActivityRegistrasiBinding.inflate(layoutInflater)
        setContentView(registrasiBinding.root)

        registrasiBinding.etName.typeEditText = "name"
        registrasiBinding.etEmail.typeEditText = "email"
        registrasiBinding.etPassword.typeEditText = "password"

        btnIntent = findViewById(R.id.login)
        btnIntent.setOnClickListener(this)

        registrasiBinding.btnRegis.setOnClickListener{
            val Iemail = registrasiBinding.etEmail.text.toString()
            val Ipassword = registrasiBinding.etPassword.text.toString()
            val Iname = registrasiBinding.etName.text.toString()
            RegisUser(Iname,Iemail,Ipassword)
        }

    }

    private fun RegisUser(Iname: String?, Iemail: String?, Ipasword: String?){

        showTunggu(true)

        val ClientApi = RetrofitClient.getRetrofitClient().getRegistrasiApi(Iname, Iemail, Ipasword)
        ClientApi.enqueue(object: Callback<RegisResponse> {
            override fun onResponse(
                call: Call<RegisResponse>,
                response: Response<RegisResponse>
            ) {
                showTunggu(false)
                val responTObody = response.body()
                Log.d(EXTRA_REGIS, "onResponse: $responTObody")
                if(response.isSuccessful && responTObody?.message == "User created") {
                    Toast.makeText(this@Registrasi, getString(R.string.regisSuc), Toast.LENGTH_SHORT).show()
                    val regisIntent = Intent(this@Registrasi, Login::class.java)
                    startActivity(regisIntent)
                    finish()
                } else {
                    Log.e(EXTRA_REGIS, "Fail: ${response.message()}")
                    Toast.makeText(this@Registrasi, getString(R.string.regisFail), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisResponse>, t: Throwable) {
                showTunggu(false)
                Log.e(EXTRA_REGIS, "Fail: ${t.message}")
                Toast.makeText(this@Registrasi, getString(R.string.regisFail), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showTunggu(status: Boolean){
        if(status){
            registrasiBinding.prgrsbar.visibility= View.VISIBLE
        }else{
            registrasiBinding.prgrsbar.visibility= View.GONE
        }
    }

    override fun onClick(v: View){
        when(v.id){
            R.id.login -> run {
                val intentToLogin = Intent(this@Registrasi, Login::class.java)
                startActivity(intentToLogin)
            }
        }
    }
}