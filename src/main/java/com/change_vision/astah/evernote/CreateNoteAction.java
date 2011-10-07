package com.change_vision.astah.evernote;


import java.io.File;

import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IMindMapDiagram;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class CreateNoteAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
	    try {
	        ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
	        projectAccessor.getProject();
	        IDiagram currentDiagram = projectAccessor.getViewManager().getDiagramViewManager().getCurrentDiagram();
			String path = currentDiagram.exportImage("/Users//tmp", "png", 96);
			Evernote evernote = new Evernote();
	        if (currentDiagram instanceof IMindMapDiagram) {
				IMindMapDiagram mmDiagram = (IMindMapDiagram) currentDiagram;
				INodePresentation root = mmDiagram.getRoot();
				String title = root.getLabel();
				StringBuilder builder = new StringBuilder();
				getContents(builder,0,root);
				builder.append(path);
				if(evernote.initialize("","")){
					evernote.createNote(title, builder.toString());
				}
				
			}
	    } catch (ProjectNotFoundException e) {
	        String message = "Project is not opened.Please open the project or create new project.";
			JOptionPane.showMessageDialog(window.getParent(), message, "Warning", JOptionPane.WARNING_MESSAGE); 
	    } catch (Exception e) {
	    	JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE); 
	        throw new UnExpectedException();
	    }
	    return null;
	}

	private void getContents(StringBuilder builder, int level, INodePresentation node) {
		String label = node.getLabel();
		builder.append(level);
		for(int i = 0; i < level; i++){
			builder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		builder.append(label);
		builder.append("<br/>");
		INodePresentation[] children = node.getChildren();
		int next = level + 1;
		for (INodePresentation child : children) {
			getContents(builder,next,child);
		}
	}


}
