package com.panic.ui.home.pages.responders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.panic.databinding.FragmentMainBinding
import com.panic.ui.base.BaseFragment
import com.panic.ui.main.MainActivity
import com.panic.ui.home.pages.view.InstalledAppsAdapter
import com.panic.ui.main.MainViewModel
import com.panic.ui.utils.SimpleDividerItemDecoration
import info.guardianproject.panic.Panic
import info.guardianproject.panic.PanicTrigger
import info.guardianproject.panic.PanicUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A placeholder fragment containing a simple view.
 */
class RespondersFragment : BaseFragment() {

    private val viewModel: RespondersViewModel by viewModel()
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private val adapter = InstalledAppsAdapter().apply {
        onClickListener = { installedApp ->
            val intent = PanicUtils.buildConnectIntent()
            intent.setPackage(installedApp.packageName)
            intent.putExtra(MainActivity.CONNECT_PACKAGE_NAME, installedApp.packageName)
            // TODO add TrustedIntents here
            ActivityCompat.startActivityForResult(requireActivity(), intent, MainActivity.CONNECT_RESULT, null)
        }
        onSwitchEnableListener = { installedApp, enabled ->
            if (enabled) {
                PanicTrigger.enableResponder(requireContext(), installedApp.packageName)
            } else {
                PanicTrigger.disableResponder(requireContext(), installedApp.packageName)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.list.observe(viewLifecycleOwner) {
            adapter.setList(it)
        }

        binding.recyclerView.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        binding.recyclerView.setHasFixedSize(true) // does not change, except in onResume()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = adapter
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): RespondersFragment {
            return RespondersFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}