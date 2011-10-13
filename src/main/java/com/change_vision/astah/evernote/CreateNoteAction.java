package com.change_vision.astah.evernote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IMindMapDiagram;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;

public class CreateNoteAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
		try {
			ProjectAccessor projectAccessor = ProjectAccessorFactory
					.getProjectAccessor();
			projectAccessor.getProject();
			IDiagram currentDiagram = projectAccessor.getViewManager()
					.getDiagramViewManager().getCurrentDiagram();
			String dirPath = "/tmp";
			String fileName = currentDiagram.exportImage(dirPath, "png", 96);
			File imageFile = new File(dirPath, fileName);
			String mimeType = "image/png";

			Resource resource = new Resource();
			resource.setData(readFileAsData(imageFile));
			resource.setMime(mimeType);
			ResourceAttributes attributes = new ResourceAttributes();
			attributes.setFileName(fileName);
			resource.setAttributes(attributes);

			Evernote evernote = new Evernote();
			if (evernote.initialize("", "")) {
				evernote.createNote(currentDiagram.getName(),resource);
			}
			imageFile.deleteOnExit();
		} catch (ProjectNotFoundException e) {
			String message = "Project is not opened.Please open the project or create new project.";
			JOptionPane.showMessageDialog(window.getParent(), message,
					"Warning", JOptionPane.WARNING_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(window.getParent(),
					"Unexpected error has occurred.", "Alert",
					JOptionPane.ERROR_MESSAGE);
			throw new UnExpectedException();
		}
		return null;
	}

	private void getContents(StringBuilder builder, int level,
			INodePresentation node) {
		String label = node.getLabel();
		builder.append(level);
		for (int i = 0; i < level; i++) {
			builder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		builder.append(label);
		builder.append("<br/>");
		INodePresentation[] children = node.getChildren();
		int next = level + 1;
		for (INodePresentation child : children) {
			getContents(builder, next, child);
		}
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
		data.setBodyHash(MessageDigest.getInstance("MD5").digest(body));
		data.setBody(body);

		return data;
	}

}
