package com.ahmetakca.aurum.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmetakca.aurum.NetworkClient
import com.ahmetakca.aurum.R
import com.ahmetakca.aurum.adapter.SnaphotPagerAdapter
import com.ahmetakca.aurum.adapter.SnapshotListAdapter
import com.ahmetakca.aurum.databinding.FragmentHomeBinding
import com.ahmetakca.aurum.models.BinanceBookTickerResponse
import com.ahmetakca.aurum.models.SnapshotRequest
import com.ahmetakca.aurum.service.ApiService
import com.ahmetakca.aurum.viewmodel.SnapshotViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.math.BigDecimal
import java.math.RoundingMode

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val client = OkHttpClient()
    private var websocket: WebSocket? = null
    private val gson = Gson()
    private var bid = BigDecimal.ZERO
    private var ask = BigDecimal.ZERO
    private var gramGoldPrice = BigDecimal.ZERO
    private var quarterGoldPrice = BigDecimal.ZERO
    private var halfGoldPrice = BigDecimal.ZERO
    private var exactGoldPrice = BigDecimal.ZERO
    private val fineness = BigDecimal("0.9166")
    private val viewModel: SnapshotViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isSaving.observe(viewLifecycleOwner) { isLoading ->
            setSaveLoading(isLoading)
        }
        setGoldLoading(true)
        connectWebSocket()
        binding.viewPager.adapter = SnaphotPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Kapanış"
                1 -> tab.text = "Kayıtlarım"
            }
        }.attach()
        binding.saveButton.setOnClickListener {
            if (bid == BigDecimal.ZERO || ask == BigDecimal.ZERO) return@setOnClickListener
            val gramBuyPrice = bid.divide(BigDecimal("31.1034768"), 8, RoundingMode.HALF_UP)
            val gramSellPrice = ask.divide(BigDecimal("31.1034768"), 8, RoundingMode.HALF_UP)
            val snapshotRequest = SnapshotRequest(
                gramBuy = gramBuyPrice,
                gramSell = gramSellPrice,
                quarterBuy = gramBuyPrice * BigDecimal("1.754") * fineness,
                quarterSell = gramSellPrice * BigDecimal("1.754") * fineness,
                halfBuy = gramBuyPrice * BigDecimal("3.508") * fineness,
                halfSell = gramSellPrice * BigDecimal("3.508") * fineness,
                fullBuy = gramBuyPrice * BigDecimal("7.016") * fineness,
                fullSell = gramSellPrice * BigDecimal("7.016") * fineness
            )
            viewModel.addSnapshot(snapshotRequest)//addSnapshot(snapshotRequest)
        }
    }

    private fun connectWebSocket() {
        val request =
            Request.Builder().url("wss://stream.binance.com:9443/ws/xauttry@bookTicker").build()
        websocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                println("Bağlantı kuruldu")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                val data = gson.fromJson(text, BinanceBookTickerResponse::class.java)
                bid = data.b.toBigDecimalOrNull() ?: return
                ask = data.a.toBigDecimalOrNull() ?: return
                val xautPrice = (bid + ask) / BigDecimal("2")
                gramGoldPrice = xautPrice / BigDecimal("31.1034768")
                quarterGoldPrice = gramGoldPrice * BigDecimal("1.754") * fineness
                halfGoldPrice = gramGoldPrice * BigDecimal("3.508") * fineness
                exactGoldPrice = gramGoldPrice * BigDecimal("7.016") * fineness

                requireActivity().runOnUiThread {
                    binding.gramTextView.text = "%.2f".format(gramGoldPrice)
                    binding.quarterTextView.text = "%.2f".format(quarterGoldPrice)
                    binding.halfTextView.text = "%.2f".format(halfGoldPrice)
                    binding.exactTextView.text = "%.2f".format(exactGoldPrice)
                    setGoldLoading(false)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                println("WebSocket hatası: ${t.message}")
            }
        })
    }

    private fun setGoldLoading(isLoading: Boolean) {
        binding.goldProgressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        binding.goldGridLayout.visibility =
            if (isLoading) View.INVISIBLE else View.VISIBLE
    }

    private fun setSaveLoading(isLoading: Boolean) {
        binding.saveProgressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE
        binding.saveButton.isEnabled = !isLoading
        binding.saveButton.text =
            if (isLoading) "" else "Kayıt Tut"
    }

    private fun setRecyclerLoading(isLoading: Boolean) {
        //binding.recyclerProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        //binding.recyclerView.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        websocket?.close(1000, "Fragment destroyed")
        websocket = null
    }
}