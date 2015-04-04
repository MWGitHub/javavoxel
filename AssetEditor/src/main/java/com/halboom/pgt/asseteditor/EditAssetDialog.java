package com.halboom.pgt.asseteditor;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditAssetDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField filePathField;

    /**
     * Label for the size amount.
     */
    private JLabel labelSizeAmount;

    /**
     * Label for the type.
     */
    private JLabel labelType;

    /**
     * Callback that runs on dialog commit.
     */
    private AssetEditorCallbacks commitCallback;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			EditAssetDialog dialog = new EditAssetDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public EditAssetDialog() {
		setBounds(100, 100, 450, 190);
		getContentPane().setLayout(new MigLayout("", "[434px,grow]", "[228px,grow][33px]"));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, "cell 0 0,grow");
		contentPanel.setLayout(new MigLayout("", "[64.00][grow][][][][]", "[][][]"));
		{
			JLabel lblFilePath = new JLabel("File Path");
			contentPanel.add(lblFilePath, "cell 0 0");
		}
		{
			filePathField = new JTextField();
			contentPanel.add(filePathField, "cell 1 0 5 1,growx");
			filePathField.setColumns(10);
		}
		{
			JLabel lblType = new JLabel("Type");
			contentPanel.add(lblType, "cell 0 1");
		}
		{
			labelType = new JLabel("Texture");
			contentPanel.add(labelType, "cell 1 1");
		}
		{
			JLabel lblSize = new JLabel("Size");
			contentPanel.add(lblSize, "cell 0 2");
		}
		{
			labelSizeAmount = new JLabel("0 KB");
			contentPanel.add(labelSizeAmount, "flowx,cell 1 2");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, "cell 0 1,growx,aligny top");
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
                        commitCallback.onAction();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
				buttonPane.add(cancelButton);
			}
		}
	}

    /**
     * @param path the path to set in the text area.
     */
    public void setPath(String path) {
        filePathField.setText(path);
    }

    /**
     * @param sizeInKB the size in KB to display.
     */
    public void setSize(long sizeInKB) {
        labelSizeAmount.setText(sizeInKB + " KB");
    }

    /**
     * @param type the type to display.
     */
    public void setType(String type) {
        labelType.setText(type);
    }

    /**
     * @return the path of the dialog.
     */
    public String getPath() {
        return filePathField.getText();
    }

    /**
     * @param commitCallback the callback to run when the commit button is pressed.
     */
    public void setCommitCallback(AssetEditorCallbacks commitCallback) {
        this.commitCallback = commitCallback;
    }
}
