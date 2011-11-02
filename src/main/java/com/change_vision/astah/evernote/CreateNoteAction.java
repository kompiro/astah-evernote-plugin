package com.change_vision.astah.evernote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.SystemUtils;

import com.change_vision.astah.evernote.dialog.LoginDialog;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;

public class CreateNoteAction implements IPluginActionDelegate {

	private static final String IMAGE_PNG = "image/png"; //$NON-NLS-1$

	public Object run(IWindow window) throws UnExpectedException {
		LoginDialog dialog = new LoginDialog(window.getParent());
		dialog.setVisible(true);
		if(dialog.isCanceled()) return null;
		try {
			ProjectAccessor projectAccessor = ProjectAccessorFactory
					.getProjectAccessor();
			projectAccessor.getProject();
			IDiagram currentDiagram = projectAccessor.getViewManager()
					.getDiagramViewManager().getCurrentDiagram();
			String dirPath = SystemUtils.JAVA_IO_TMPDIR;
			if(SystemUtils.IS_OS_MAC_OSX){
				// Macの場合java.io.tmpdirだと個人毎のフォルダになってしまうため。
				dirPath = "/tmp"; //$NON-NLS-1$
			}
			String fileName = currentDiagram.exportImage(dirPath, "png", 96); //$NON-NLS-1$
			Resource resource = createResource(dirPath, fileName);

			Evernote evernote = new Evernote();
			
			String username = dialog.getUsername();
			String password = dialog.getPassword();
			if (evernote.initialize(username, password)) {
				evernote.createNote(currentDiagram.getName(),resource);
				JOptionPane.showMessageDialog(window.getParent(), Messages.getString("CreateNoteAction.success")); //$NON-NLS-1$
			} else{
				JOptionPane.showMessageDialog(window.getParent(), Messages.getString("CreateNoteAction.login_error_title"),Messages.getString("CreateNoteAction.warning_title"), JOptionPane.WARNING_MESSAGE);				 //$NON-NLS-1$ //$NON-NLS-2$
			}
			
		} catch (ProjectNotFoundException e) {
			String message = Messages.getString("CreateNoteAction.project_not_found_error"); //$NON-NLS-1$
			JOptionPane.showMessageDialog(window.getParent(), message,
					Messages.getString("CreateNoteAction.warning_title"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
		} catch (Exception e) {
			JOptionPane.showMessageDialog(window.getParent(),
					Messages.getString("CreateNoteAction.error_message"), Messages.getString("CreateNoteAction.alert_title"), //$NON-NLS-1$ //$NON-NLS-2$
					JOptionPane.ERROR_MESSAGE);
			throw new UnExpectedException();
		}
		return null;
	}

	private Resource createResource(String dirPath, String fileName)
			throws Exception {
		File imageFile = new File(dirPath, fileName);
		Resource resource = new Resource();
		resource.setData(readFileAsData(imageFile));
		resource.setMime(IMAGE_PNG);
		ResourceAttributes attributes = new ResourceAttributes();
		attributes.setFileName(fileName);
		resource.setAttributes(attributes);
		imageFile.deleteOnExit();
		return resource;
	}

	private Data readFileAsData(File file) throws Exception {

		// Read the full binary contents of the file
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byte[] block = new byte[10240];
		int len;
		while ((len = in.read(block)) >= 0) {
			byteOut.write(block, 0, len);
		}
		in.close();
		byte[] body = byteOut.toByteArray();

		// Create a new Data object to contain the file contents
		Data data = new Data();
		data.setSize(body.length);
		data.setBodyHash(MessageDigest.getInstance("MD5").digest(body)); //$NON-NLS-1$
		data.setBody(body);

		return data;
	}

}
