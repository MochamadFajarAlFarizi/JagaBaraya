package com.fajar.jagabaraya

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit


class ProfileFragment : Fragment() {

    private lateinit var ivLogout: ImageView
    private lateinit var menuEditProfil: LinearLayout
    private lateinit var menuLaporanku: LinearLayout
    private lateinit var menuBahasa: LinearLayout
    private lateinit var tvNama: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivLogout = view.findViewById(R.id.ivLogout)
        menuEditProfil = view.findViewById(R.id.menuEditProfil)
        menuLaporanku = view.findViewById(R.id.menuLaporanku)
        menuBahasa = view.findViewById(R.id.menuBahasa)
        tvNama= view.findViewById(R.id.tvNama)

        tvNama.text = "Fajar Ganteng"

        // Tombol Logout
        ivLogout.setOnClickListener {
            showLogoutDialog()
        }

        // Menu Edit Profil -> buka fragment EditProfil
        menuEditProfil.setOnClickListener {
            openFragment(EditProfilFragment())
        }

        // Menu Laporanku -> buka fragment Laporanku
        menuLaporanku.setOnClickListener {
            openFragment(LaporankuFragment())
        }

        // Menu Bahasa -> buka setting bahasa sistem
        menuBahasa.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(id, fragment) // id = id dari fragment_profile.xml yang sedang dipasang
            .addToBackStack(null)
            .commit()
    }
}
