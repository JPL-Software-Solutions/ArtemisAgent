package artemis.agent.setup

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import artemis.agent.AgentViewModel
import artemis.agent.ConnectionStatus
import artemis.agent.R
import artemis.agent.UserSettingsSerializer.userSettings
import artemis.agent.databinding.ConnectFragmentBinding
import artemis.agent.databinding.fragmentViewBinding
import artemis.agent.generic.GenericDataAdapter
import artemis.agent.generic.GenericDataEntry
import artemis.agent.util.SoundEffect
import artemis.agent.util.collectLatestWhileStarted
import com.walkertribe.ian.protocol.udp.PrivateNetworkType
import dev.tmapps.konnection.Konnection
import java.net.InetAddress
import java.net.NetworkInterface

class ConnectFragment : Fragment(R.layout.connect_fragment) {
    private val viewModel: AgentViewModel by activityViewModels()
    private val binding: ConnectFragmentBinding by fragmentViewBinding()

    private val recentAdapter: RecentServersAdapter by lazy {
        RecentServersAdapter(binding.root.context)
    }

    private val networkTypes: Array<String> by lazy {
        binding.root.resources.getStringArray(R.array.network_type_entries)
    }

    private var playSoundsOnTextChange: Boolean = false
    private var playSoundOnScanFinished: Boolean = false
    private var networkAddress: String? = null
    private var broadcastAddress: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.settingsPage.value = null

        binding.root.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideKeyboard()
            }
        }

        prepareInfoLabels()
        prepareConnectionSection()
        prepareScanningSection()

        viewLifecycleOwner.collectLatestWhileStarted(view.context.userSettings.data) {
            recentAdapter.filter.servers =
                it.recentServersList.apply {
                    var addressText = firstOrNull() ?: ""
                    if (viewModel.connectedUrl.value.isBlank()) {
                        viewModel.addressBarText.also { text ->
                            if (text.isNotBlank()) {
                                addressText = text
                            }
                        }
                    }
                    playSoundsOnTextChange = false
                    binding.addressBar.setText(addressText)
                }
        }
    }

    override fun onPause() {
        val addressBar = binding.addressBar
        viewModel.addressBarText = addressBar.text.toString()
        addressBar.clearFocus()
        hideKeyboard()

        super.onPause()
    }

    private fun prepareInfoLabels() {
        viewLifecycleOwner.collectLatestWhileStarted(binding.root.context.userSettings.data) {
            val isShowing = it.showNetworkInfo
            viewModel.showingNetworkInfo = isShowing

            val visibility = if (isShowing) View.VISIBLE else View.GONE
            binding.addressLabel.visibility = visibility
            binding.networkTypeLabel.visibility = visibility
            binding.networkInfoDivider.visibility = visibility
        }

        viewLifecycleOwner.collectLatestWhileStarted(
            Konnection.instance.observeNetworkConnection()
        ) {
            val info = Konnection.instance.getInfo()
            val address = info?.ipv4
            networkAddress = address?.takeIf { PrivateNetworkType.of(it) != null }
            broadcastAddress = null

            binding.networkTypeLabel.text =
                info?.let { networkTypes[it.connection.ordinal] }
                    ?: binding.root.context.getString(R.string.network_not_found)
            binding.addressLabel.text = address
        }
    }

    private fun prepareConnectionSection() {
        binding.connectButton.setOnClickListener {
            viewModel.activateHaptic()
            hideKeyboard()
            viewModel.connectToServer()
        }

        val addressBar = binding.addressBar
        addressBar.setAdapter(recentAdapter)

        addressBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.activateHaptic()
                viewModel.playSound(SoundEffect.BEEP_2)
            }
        }

        addressBar.setOnClickListener {
            viewModel.activateHaptic()
            viewModel.playSound(SoundEffect.BEEP_2)
        }

        viewLifecycleOwner.collectLatestWhileStarted(viewModel.connectionStatus) {
            binding.connectLabel.text = getString(it.stringId)
            binding.connectBar.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, it.color)
            )
            binding.connectSpinner.visibility = it.spinnerVisibility

            if (!viewModel.attemptingConnection && it is ConnectionStatus.Connecting) {
                addressBar.clearFocus()

                val url = addressBar.text.toString()
                viewModel.tryConnect(url)
            }
        }

        addressBar.addTextChangedListener {
            if (playSoundsOnTextChange) {
                viewModel.playSound(SoundEffect.BEEP_2)
            } else {
                playSoundsOnTextChange = true
            }

            binding.connectButton.isEnabled = !it.isNullOrBlank()
        }
    }

    private fun prepareScanningSection() {
        val serverListAdapter = GenericDataAdapter {
            viewModel.activateHaptic()
            playSoundsOnTextChange = false
            binding.addressBar.setText(it.data)
            viewModel.connectToServer()
        }
        binding.serverList.apply {
            itemAnimator = null
            adapter = serverListAdapter
        }

        val scanButton = binding.scanButton
        val scanSpinner = binding.scanSpinner
        val noServersLabel = binding.noServersLabel

        scanButton.setOnClickListener {
            viewModel.activateHaptic()
            viewModel.playSound(SoundEffect.BEEP_2)
            hideKeyboard()
            checkForBroadcastAddress()
            viewModel.scanForServers(broadcastAddress)
        }

        viewLifecycleOwner.collectLatestWhileStarted(viewModel.discoveredServers) {
            serverListAdapter.onListUpdate(
                it.map { (ip, hostName) -> GenericDataEntry(hostName, ip) }
            )
        }

        viewLifecycleOwner.collectLatestWhileStarted(viewModel.isScanningUDP) {
            scanButton.isEnabled = !it
            if (it) {
                binding.addressBar.clearFocus()
                scanSpinner.visibility = View.VISIBLE
                noServersLabel.visibility = View.GONE
            } else {
                if (playSoundOnScanFinished) {
                    viewModel.playSound(SoundEffect.BEEP_1)
                }
                scanSpinner.visibility = View.GONE
                noServersLabel.visibility =
                    if (serverListAdapter.itemCount == 0) {
                        noServersLabel.setText(R.string.no_servers_found)
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
            playSoundOnScanFinished = true
        }
    }

    private fun hideKeyboard() {
        viewModel.hideKeyboard(binding.root)
    }

    private fun checkForBroadcastAddress() {
        if (broadcastAddress != null) return
        val networkInterface =
            NetworkInterface.getByInetAddress(InetAddress.getByName(networkAddress)) ?: return

        broadcastAddress =
            networkInterface.interfaceAddresses
                .firstOrNull { it.address?.hostAddress == networkAddress }
                ?.broadcast
                ?.hostAddress
    }

    private class RecentServersAdapter(context: Context) :
        ArrayAdapter<String>(context, R.layout.generic_data_entry, R.id.entryNameLabel) {
        val filter = RecentServersFilter(this)

        override fun getCount(): Int = filter.suggestions.size

        override fun getItem(position: Int): String = filter.suggestions[position]

        override fun getFilter(): Filter = filter
    }

    private class RecentServersFilter(private val adapter: RecentServersAdapter) : Filter() {
        var servers: List<String> = emptyList()
        private val mutSuggestions: MutableList<String> = mutableListOf()
        val suggestions: List<String>
            get() = mutSuggestions

        override fun performFiltering(constraint: CharSequence?): FilterResults =
            FilterResults().apply {
                val results =
                    if (constraint.isNullOrBlank()) {
                        servers
                    } else {
                        val regex =
                            Regex(
                                constraint
                                    .split('.')
                                    .filter(String::isNotBlank)
                                    .joinToString(".*\\..*") { Regex.escape(it) }
                            )
                        val dotCount = constraint.count { it == '.' }
                        servers.filter { server ->
                            regex.containsMatchIn(server) && server.count { it == '.' } >= dotCount
                        }
                    }

                values = results.joinToString("\n")
                count = results.size
            }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            mutSuggestions.clear()
            if (results.count > 0) {
                mutSuggestions.addAll(results.values.toString().split('\n'))
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyDataSetInvalidated()
            }
        }
    }
}
