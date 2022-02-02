package com.panic.ui.home.pages.triggers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.panic.databinding.FragmentMainBinding
import com.panic.ui.base.BaseFragment
import com.panic.ui.home.pages.view.InstalledAppsAdapter
import com.panic.ui.utils.SimpleDividerItemDecoration
import info.guardianproject.panic.PanicResponder

/**
 * A placeholder fragment containing a simple view.
 */
class TriggersFragment : BaseFragment() {

    private lateinit var pageViewModel: TriggersViewModel
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val adapter = InstalledAppsAdapter().apply {
        onClickListener = { installedApp ->
//                requestPackageName = installedApp
//                val intent = Intent(Panic.ACTION_CONNECT)
//                intent.setPackage(requestPackageName)
//                // TODO add TrustedIntents here
//                ActivityCompat.startActivityForResult(this, intent, MainActivity.CONNECT_RESULT, null)
        }
        onSwitchEnableListener = { installedApp, enabled ->
            if (enabled) {
                PanicResponder.addTriggerPackageName(requireActivity(), installedApp.packageName)
            } else {
                PanicResponder.removeTriggerPackageName(requireActivity(), installedApp.packageName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this)[TriggersViewModel::class.java].apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }

        lifecycle.addObserver(pageViewModel)
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
        pageViewModel.list.observe(viewLifecycleOwner) {
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
        fun newInstance(sectionNumber: Int): TriggersFragment {
            return TriggersFragment().apply {
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