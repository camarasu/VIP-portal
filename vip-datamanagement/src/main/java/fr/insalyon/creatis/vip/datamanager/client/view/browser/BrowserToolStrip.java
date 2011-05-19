/* Copyright CNRS-CREATIS
 *
 * Rafael Silva
 * rafael.silva@creatis.insa-lyon.fr
 * http://www.creatis.insa-lyon.fr/~silva
 *
 * This software is a grid-enabled data-driven workflow manager and editor.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.insalyon.creatis.vip.datamanager.client.view.browser;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import fr.insalyon.creatis.vip.common.client.view.Context;
import fr.insalyon.creatis.vip.common.client.view.modal.ModalWindow;
import fr.insalyon.creatis.vip.datamanager.client.DataManagerConstants;
import fr.insalyon.creatis.vip.datamanager.client.rpc.FileCatalogService;
import fr.insalyon.creatis.vip.datamanager.client.rpc.FileCatalogServiceAsync;
import fr.insalyon.creatis.vip.datamanager.client.rpc.TransferPoolService;
import fr.insalyon.creatis.vip.datamanager.client.rpc.TransferPoolServiceAsync;
import fr.insalyon.creatis.vip.datamanager.client.view.common.BasicBrowserToolStrip;
import fr.insalyon.creatis.vip.datamanager.client.view.operation.OperationLayout;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rafael Silva
 */
public class BrowserToolStrip extends BasicBrowserToolStrip {

    public BrowserToolStrip(final ModalWindow modal) {
        
        super(modal);
        
        ToolStripButton addFolderButton = new ToolStripButton();
        addFolderButton.setIcon("icon-addfolder.png");
        addFolderButton.setPrompt("Create Folder");
        addFolderButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                String path = pathItem.getValueAsString();
                if (path.equals(DataManagerConstants.ROOT)) {
                    SC.warn("You cannot create a folder in the root folder.");
                } else {
                    new AddFolderWindow(modal, path).show();
                }
            }
        });
        this.addButton(addFolderButton);

        this.addSeparator();
        ToolStripButton uploadButton = new ToolStripButton();
        uploadButton.setIcon("icon-upload.png");
        uploadButton.setPrompt("Upload File");
        uploadButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                String path = pathItem.getValueAsString();
                if (path.equals(DataManagerConstants.ROOT)) {
                    SC.warn("You cannot upload a file in the root folder.");
                } else {
                    new FileUploadWindow(modal, path).show();
                }
            }
        });
        this.addButton(uploadButton);

        ToolStripButton downloadButton = new ToolStripButton();
        downloadButton.setIcon("icon-download.png");
        downloadButton.setPrompt("Download Selected Files");
        downloadButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                downloadFiles();
            }
        });
        this.addButton(downloadButton);

        this.addSeparator();
        ToolStripButton deleteButton = new ToolStripButton();
        deleteButton.setIcon("icon-delete.png");
        deleteButton.setPrompt("Delete Selected Files/Folders");
        deleteButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                String path = pathItem.getValueAsString();
                if (path.equals(DataManagerConstants.ROOT)) {
                    SC.warn("You cannot delete a root folder.");
                } else {
                    delete();
                }
            }
        });
        this.addButton(deleteButton);
    }

    private void downloadFiles() {
        ListGridRecord[] records = BrowserLayout.getInstance().getGridSelection();

        for (ListGridRecord record : records) {
            DataRecord data = (DataRecord) record;
            if (data.getType().contains("file")) {
                TransferPoolServiceAsync service = TransferPoolService.Util.getInstance();
                AsyncCallback<Void> callback = new AsyncCallback<Void>() {

                    public void onFailure(Throwable caught) {
                        modal.hide();
                        SC.warn("Unable to download file: " + caught.getMessage());
                    }

                    public void onSuccess(Void result) {
                        modal.hide();
                        OperationLayout.getInstance().loadData();
                    }
                };
                modal.show("Adding files to transfer queue...", true);
                Context context = Context.getInstance();
                service.downloadFile(
                        context.getUser(),
                        pathItem.getValueAsString() + "/" + data.getName(),
                        context.getUserDN(), context.getProxyFileName(),
                        callback);
            }
        }
    }

    private void delete() {
        ListGridRecord[] records = BrowserLayout.getInstance().getGridSelection();
        final List<String> paths = new ArrayList<String>();

        for (ListGridRecord record : records) {
            DataRecord data = (DataRecord) record;
            paths.add(pathItem.getValueAsString() + "/" + data.getName());
        }
        SC.confirm("Do you really want to delete the files/folders \"" + paths + "\"?", new BooleanCallback() {

            public void execute(Boolean value) {
                if (value != null && value) {
                    FileCatalogServiceAsync service = FileCatalogService.Util.getInstance();
                    AsyncCallback<Void> callback = new AsyncCallback<Void>() {

                        public void onFailure(Throwable caught) {
                            modal.hide();
                            SC.warn("Error executing delete files/folders: " + caught.getMessage());
                        }

                        public void onSuccess(Void result) {
                            modal.hide();
                            BrowserLayout.getInstance().loadData(pathItem.getValueAsString(), true);
                        }
                    };
                    modal.show("Deleting files/folders...", true);
                    Context context = Context.getInstance();
                    service.deleteFiles(context.getUser(), context.getProxyFileName(), 
                            paths, callback);
                }
            }
        });
    }
}
