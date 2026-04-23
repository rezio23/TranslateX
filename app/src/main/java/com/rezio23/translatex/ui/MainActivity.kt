package com.rezio23.translatex.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rezio23.translatex.databinding.ActivityMainBinding
import com.rezio23.translatex.model.SUPPORTED_LANGUAGES

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TranslateViewModel by viewModels()

    // Languages without "Auto Detect" for target spinner
    private val targetLanguages = SUPPORTED_LANGUAGES.filter { it.code != "auto" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupSpinners() {
        // Source language spinner (includes Auto)
        val sourceNames = SUPPORTED_LANGUAGES.map { it.name }
        val sourceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sourceNames)
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSourceLang.adapter = sourceAdapter
        binding.spinnerSourceLang.setSelection(0) // Auto Detect

        binding.spinnerSourceLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setSourceLang(SUPPORTED_LANGUAGES[position].code)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Target language spinner (no Auto)
        val targetNames = targetLanguages.map { it.name }
        val targetAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, targetNames)
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTargetLang.adapter = targetAdapter
        // Default: Spanish (index 0 in targetLanguages = English, 1 = Spanish)
        val defaultTargetIndex = targetLanguages.indexOfFirst { it.code == "es" }
        binding.spinnerTargetLang.setSelection(if (defaultTargetIndex >= 0) defaultTargetIndex else 0)

        binding.spinnerTargetLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setTargetLang(targetLanguages[position].code)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupClickListeners() {
        binding.btnTranslate.setOnClickListener {
            val text = binding.etSourceText.text.toString().trim()
            viewModel.translate(text)
        }

        binding.btnSwapLanguages.setOnClickListener {
            val sourceText = binding.etSourceText.text.toString()
            val targetText = binding.tvTranslatedText.text.toString()
            val (newSource, newTarget) = viewModel.swapLanguages(sourceText, targetText)
            binding.etSourceText.setText(newSource)
            binding.tvTranslatedText.text = newTarget

            // Update spinner selections
            val newSrc = viewModel.sourceLang.value ?: "auto"
            val newTgt = viewModel.targetLang.value ?: "en"
            val srcIdx = SUPPORTED_LANGUAGES.indexOfFirst { it.code == newSrc }
            val tgtIdx = targetLanguages.indexOfFirst { it.code == newTgt }
            if (srcIdx >= 0) binding.spinnerSourceLang.setSelection(srcIdx)
            if (tgtIdx >= 0) binding.spinnerTargetLang.setSelection(tgtIdx)
        }

        binding.btnClearSource.setOnClickListener {
            binding.etSourceText.setText("")
            binding.tvTranslatedText.text = ""
        }

        binding.btnCopyResult.setOnClickListener {
            val text = binding.tvTranslatedText.text.toString()
            if (text.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("translated_text", text))
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.translateState.observe(this) { state ->
            when (state) {
                is TranslateState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                }
                is TranslateState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                    binding.tvTranslatedText.text = ""
                }
                is TranslateState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                    binding.tvTranslatedText.text = state.translatedText
                }
                is TranslateState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }
}
