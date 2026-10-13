package com.ahmetakca.aurum.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.ahmetakca.aurum.NetworkClient
import com.ahmetakca.aurum.R
import com.ahmetakca.aurum.databinding.FragmentLoginBinding
import com.ahmetakca.aurum.models.LoginRequest
import com.ahmetakca.aurum.service.ApiService
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private val compositeDisposable = CompositeDisposable()
    private lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        credentialManager = CredentialManager.create(requireContext())
        apiService = NetworkClient.createApiService()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (auth.currentUser != null) {
            val user = auth.currentUser
            user?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                if (tokenTask.isSuccessful) {
                    val firebaseIdToken = tokenTask.result?.token!!
                    val loginRequest = LoginRequest(
                        user.email,
                        user.displayName,
                        user.phoneNumber,
                        user.photoUrl.toString()
                    )
                    loginAurum(loginRequest, firebaseIdToken)
                }
            }
            navigateToHome()
            return
        }
        binding.googleSignInButton.setOnClickListener {
            setLoading(true)
            println("Butona tıklandı")
            launchCredentialManager()
        }
    }

    private fun launchCredentialManager() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("124343273451-i30v9r9jhg2rughqjf2s2hf8dpaqtg3k.apps.googleusercontent.com")
            .setFilterByAuthorizedAccounts(false).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(requireContext(), request)
                handleSignIn(result.credential)
            } catch (e: GetCredentialException) {
                setLoading(false)
                Log.e("launch", "Couldn't retrieve user's credentials: ${e.localizedMessage}")
            }
        }
    }

    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            setLoading(false)
            Log.e("sign", "Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Log.d("success", "signInWithCredential:success")
                val user = auth.currentUser
                user?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val firebaseIdToken = tokenTask.result?.token!!
                        val loginRequest = LoginRequest(
                            user.email,
                            user.displayName,
                            user.phoneNumber,
                            user.photoUrl.toString()
                        )
                        loginAurum(loginRequest, firebaseIdToken)
                        println("Giriş başarılı")
                    } else {
                        setLoading(false)
                        println("Token alınamadı")
                    }
                }
            } else {
                setLoading(false)
                Log.w("failure", "signInWithCredential:failure", task.exception)
            }
        }
    }

    private fun loginAurum(loginRequest: LoginRequest, uidToken: String) {
        compositeDisposable.add(
            apiService.login(loginRequest, "Bearer $uidToken")
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    setLoading(false)
                    navigateToHome()
                }, { error ->
                    setLoading(false)
                    error.printStackTrace()
                })
        )
    }

    private fun setLoading(isLoading: Boolean) {
        binding.signInProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.googleSignInButton.isEnabled = !isLoading
    }

    private fun signOut() {
        auth.signOut()
        lifecycleScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
            } catch (e: ClearCredentialException) {
                Log.e("except", "Couldn't clear user credentials: ${e.localizedMessage}")
            }
        }
    }

    private fun navigateToHome() {
        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }
}