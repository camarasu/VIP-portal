/* Copyright CNRS-CREATIS
 *
 * Rafael Silva
 * rafael.silva@creatis.insa-lyon.fr
 * http://www.rafaelsilva.com
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
package fr.insalyon.creatis.vip.simulationgui.client.view;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.HoverEvent;
import com.smartgwt.client.widgets.events.HoverHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ItemHoverEvent;
import com.smartgwt.client.widgets.form.fields.events.ItemHoverHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import fr.cnrs.i3s.neusemstore.vip.semantic.simulation.model.client.bean.SimulationObjectModel;
import fr.cnrs.i3s.neusemstore.vip.semantic.simulation.model.client.bean.SimulationObjectModelLight;
import fr.insalyon.creatis.vip.core.client.view.ModalWindow;
import fr.insalyon.creatis.vip.models.client.rpc.ModelService;
import fr.insalyon.creatis.vip.models.client.rpc.ModelServiceAsync;
import fr.insalyon.creatis.vip.simulationgui.client.SimulationGUIConstants;
import fr.insalyon.creatis.vip.simulationgui.client.bean.Data3D;
import fr.insalyon.creatis.vip.simulationgui.client.gwtgl.ObjectModel;
import fr.insalyon.creatis.vip.simulationgui.client.rpc.VTKController;
import fr.insalyon.creatis.vip.simulationgui.client.rpc.VTKControllerAsync;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *  @author Kevin Moulin, Rafael Silva
 */
public class SimulationGUITab extends Tab {

    private final ToolStrip toolStrip;
    private final DefineSceneSection defineSceneSection;
    // private final DefineParamsSection defineParamsSection;
    private VTKControllerAsync VTK = VTKController.Util.getInstance();
    private ModelServiceAsync MAP = ModelService.Util.getInstance();
    private ModalWindow modal;
    private String dynaStringTab[];
    private SelectItem modelSelectItem = new SelectItem("model");
    private Map<String, String> mapNameUri = new HashMap<String, String>();
    private String uri;
    private ToolStripButton exampleButton = new ToolStripButton("Load example");
    static private String modelStorageURL = "";
    //dans le constructeur, creer les 4 tabs. Les ajouter/enlever du Layout en fonction des cases cochees

    public SimulationGUITab() {

        this.setTitle(Canvas.imgHTML(SimulationGUIConstants.ICON_EDITOR) + " "
                + SimulationGUIConstants.APP_EDITOR);
        this.setID(SimulationGUIConstants.TAB_EDITOR);
        this.setCanClose(true);
        
        SectionStack sectionStack = new SectionStack();
        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
        sectionStack.setAnimateSections(true);
        sectionStack.setCanResizeSections(true);
        sectionStack.showSection(0);
        sectionStack.hideSection(1);
        defineSceneSection = new DefineSceneSection();
        sectionStack.setSections(defineSceneSection);

        toolStrip = new ToolStrip();
        modal = new ModalWindow(toolStrip);
        exampleButton.setIcon(SimulationGUIConstants.ICON_EXAMPLE);
        exampleButton.setActionType(SelectionType.CHECKBOX);
        toolStrip.setWidth100();
        toolStrip.setHeight(20);
        toolStrip.addButton(exampleButton);
        toolStrip.addSeparator();
        toolStrip.addFormItem(modelSelectItem);
        
        VLayout vLayout = new VLayout();
        vLayout.addMember(toolStrip);
        vLayout.addMember(sectionStack);

        this.setPane(vLayout);
        initControl();
        initRPC();
    }

    private void initRPC() {
        
        modal.show("Loading list of model from grid", true);
        
        exampleButton.addClickHandler(new ClickHandler() {

            public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
                defineSceneSection.showModal("Object making");
                String path = "/home/moulin/NetBeansProjects/Portal/vip-simulation-gui/src/main/resources";
                String path2 = "/home/moulin/ressources";

                VTK.downloadAndUnzipExample(path2, new AsyncCallback<Data3D[][]>() {

                    public void onSuccess(Data3D[][] result) {
                        defineSceneSection.hideModal();
                        SimulationGUIControlBoxModel.getInstance().setTreeNode(result);
                        ObjectModel.getInstance().addModel(result);
                    }

                    public void onFailure(Throwable caught) {
                        defineSceneSection.hideModal();
                        // Show the RPC error message to the user
                        SC.say("VTK error");
                    }
                });
            }
        });
        
        MAP.listAllModels(new AsyncCallback<List<SimulationObjectModelLight>>() {

            public void onSuccess(List<SimulationObjectModelLight> result) {
                modal.hide();
                dynaStringTab = new String[result.size()];
                int i = 0;
                for (SimulationObjectModelLight s : result) {
                    dynaStringTab[i] = s.getModelName();
                    mapNameUri.put(s.getModelName(), s.getURI());
                    i++;
                }
                modelSelectItem.setValueMap(dynaStringTab);
                toolStrip.setHeight(10);
            }

            public void onFailure(Throwable caught) {
                modelSelectItem.setValue("Model can't be load");
                modal.hide();
                toolStrip.setHeight(10);
            }
        });

        modelSelectItem.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                String selectedItem = (String) event.getValue();
                uri = mapNameUri.get(selectedItem);
                defineSceneSection.showModal("Object downloading ");
                MAP.rebuildObjectModelFromTripleStore(uri, new AsyncCallback<SimulationObjectModel>() {

                    public void onSuccess(final SimulationObjectModel result) {
                        modelStorageURL = result.getStorageURL();
                        defineSceneSection.showModal("Object making");
                        VTK.downloadAndUnzipModel(modelStorageURL, new AsyncCallback<Data3D[][]>() {

                            public void onSuccess(Data3D[][] result2) {
                                defineSceneSection.hideModal();
                                ObjectModel.getInstance().addModel(result2);
                                SimulationGUIControlBoxModel.getInstance().setTreeNode(result2);
                                refreshLaunchTabValue();
                            }

                            public void onFailure(Throwable caught) {
                                // Show the RPC error message to the user
                                defineSceneSection.hideModal();
                                SC.say("Error during the making");
                            }
                        });
                    }

                    public void onFailure(Throwable caught) {
                        defineSceneSection.hideModal();
                        SC.say("url error");
                        modelSelectItem.setValue("Grid error");
                    }
                });
            }
        });
    }

    /**
     * Hover
     */
    private void initControl() {
        
        exampleButton.setCanHover(Boolean.TRUE);
        exampleButton.addHoverHandler(new HoverHandler() {

            public void onHover(HoverEvent event) {
                String prompt = "Show a standard model";
                exampleButton.setPrompt(prompt);
            }
        });
        modelSelectItem.addItemHoverHandler(new ItemHoverHandler() {

            public void onItemHover(ItemHoverEvent event) {
                String prompt = "Selects the model to be displayed from the grid";
                modelSelectItem.setPrompt(prompt);
            }
        });
    }

    public static String getModelStorage() {
        
        return modelStorageURL;
    }

    private void refreshLaunchTabValue() {
        
        SimulationGUIControlBox.getInstance(SimulationGUIConstants.CLASS_US).refreshLaunchTabValue();
        SimulationGUIControlBox.getInstance(SimulationGUIConstants.CLASS_MRI).refreshLaunchTabValue();
        SimulationGUIControlBox.getInstance(SimulationGUIConstants.CLASS_CT).refreshLaunchTabValue();
        SimulationGUIControlBox.getInstance(SimulationGUIConstants.CLASS_PET).refreshLaunchTabValue();
    }
}