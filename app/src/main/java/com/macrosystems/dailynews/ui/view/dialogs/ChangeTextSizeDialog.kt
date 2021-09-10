package com.macrosystems.dailynews.ui.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup

import androidx.fragment.app.DialogFragment

import com.macrosystems.dailynews.databinding.ChangeTextSizeDialogBinding

class ChangeTextSizeDialog : DialogFragment() {
    private var title: String = ""
    private var isDialogCancelable: Boolean = true
    private var positiveAction: Action = Action.Empty
    private var negativeAction: Action = Action.Empty

    companion object {
        fun create(
            title: String = "",
            isDialogCancelable: Boolean = true,
            positiveAction: Action = Action.Empty,
            negativeAction: Action = Action.Empty
        ): ChangeTextSizeDialog = ChangeTextSizeDialog().apply {
            this.title = title
            this.isDialogCancelable = isDialogCancelable
            this.positiveAction = positiveAction
            this.negativeAction = negativeAction
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return

        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = ChangeTextSizeDialogBinding.inflate(requireActivity().layoutInflater)

        with(binding){
            tvChangeTextSizeTitle.text = title

            btnSmallText.setOnClickListener {
                dismiss()
                positiveAction.onClickListener(1)
            }
            btnMidText.setOnClickListener { dismiss()
                positiveAction.onClickListener(2)
            }
            btnLargeText.setOnClickListener { dismiss()
                positiveAction.onClickListener(3)
            }
            btnCancel.setOnClickListener { dismiss() }
        }
        isCancelable = isDialogCancelable
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(isDialogCancelable)
            .create()
    }


    data class Action(val text: String, val onClickListener: (Int) -> Unit) {
        companion object {
            val Empty = Action("") {

            }
        }
    }
}



