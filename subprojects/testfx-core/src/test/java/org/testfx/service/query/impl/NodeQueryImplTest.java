package org.testfx.service.query.impl;

import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.service.query.NodeQuery;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.testfx.service.query.impl.NodeQueryUtils.bySelector;
import static org.testfx.service.query.impl.NodeQueryUtils.hasId;
import static org.testfx.service.query.impl.NodeQueryUtils.rootOfScene;

public class NodeQueryImplTest {

    //---------------------------------------------------------------------------------------------
    // FIELDS.
    //---------------------------------------------------------------------------------------------

    NodeQuery nodeQuery;

    Scene scene;

    @FXML Pane labels;
    @FXML Label label0;
    @FXML Label label1;
    @FXML Label label2;

    @FXML Pane buttons;
    @FXML Button button0;
    @FXML Button button1;
    @FXML Button button2;

    @FXML Pane panes;
    @FXML Label pane1_label2;

    //---------------------------------------------------------------------------------------------
    // FIXTURE METHODS.
    //---------------------------------------------------------------------------------------------

    @BeforeClass
    public static void setupSpec() throws Exception {
        FxToolkit.registerPrimaryStage();
    }

    @Before
    public void setup() throws Exception {
        nodeQuery = new NodeQueryImpl();

        FxToolkit.setupStage((stage) -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("res/nodeQueryImpl.fxml"));
            loader.setController(this);
            try {
                scene = new Scene(loader.load());
            }
            catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    // FEATURE METHODS.
    //---------------------------------------------------------------------------------------------

    @Test
    public void queryAll() {
        // when:
        Set<Node> result = nodeQuery.from(label0, label1, label2)
            .queryAll();

        // then:
        assertThat(result, contains(label0, label1, label2));
    }

    @Test
    public void queryFirst() {
        // when:
        Optional<Node> result = nodeQuery.from(label0, label1, label2)
            .queryFirst();

        // then:
        assertThat(result, is(Optional.of(label0)));
    }

    @Test
    public void queryFirst_absent() {
        // when:
        Optional<Node> result = nodeQuery.from()
            .queryFirst();

        // then:
        assertThat(result, is(Optional.absent()));
    }

    @Test
    public void from_label0_label1() {
        // when:
        Set<Node> result = nodeQuery.from(label0, label1)
            .queryAll();

        // then:
        assertThat(result, contains(label0, label1));
    }

    @Test
    public void from_label0_from_label1() {
        // when:
        Set<Node> result = nodeQuery.from(label0).from(label1)
            .queryAll();

        // then:
        assertThat(result, contains(label0, label1));
    }

    @Test
    public void from_label0_label0() {
        // when:
        Set<Node> result = nodeQuery.from(label0, label0)
            .queryAll();

        // then:
        assertThat(result, contains(label0));
    }

    @Test
    public void from_label0_from_label0() {
        // when:
        Set<Node> result = nodeQuery.from(label0).from(label0)
            .queryAll();

        // then:
        assertThat(result, contains(label0));
    }

    @Test
    public void lookup() {
        // when:
        Set<Node> result = nodeQuery.from(labels)
            .lookup(bySelector(".label"))
            .queryAll();

        // then:
        assertThat(result, contains(label0, label1, label2));
    }

    // TODO: lookup(bySelector(".pane")).lookup(bySelector(".label"))
    // TODO: lookup(bySelector(".label"), bySelector(".button"))

    @Test
    public void lookupAt() {
        // when:
        Set<Node> result = nodeQuery.from(labels)
            .lookupAt(1, bySelector(".label"))
            .queryAll();

        // then:
        assertThat(result, contains(label1));
    }

    @Test
    public void lookupAt_lookupAt() {
        // when:
        Set<Node> result = nodeQuery.from(panes)
            .lookupAt(1, bySelector(".pane"))
            .lookupAt(2, bySelector(".label"))
            .queryAll();

        // then:
        assertThat(result, contains(pane1_label2));
    }

    @Test
    public void lookupAt_with_index_out_of_bounds() {
        // when:
        Set<Node> result = nodeQuery.from(labels)
            .lookupAt(99, bySelector(".label"))
            .queryAll();

        // then:
        assertThat(result, empty());
    }

    @Test
    public void lookupAt_lookupAt_with_indizes_out_of_bounds() {
        // when:
        Set<Node> result = nodeQuery.from(panes)
            .lookupAt(99, bySelector(".pane"))
            .lookupAt(99, bySelector(".label"))
            .queryAll();

        // then:
        assertThat(result, empty());
    }

    @Test
    public void match() {
        // when:
        Set<Node> result = nodeQuery.from(rootOfScene(scene))
            .lookup(bySelector(".button"))
            .match(hasId("button1"))
            .queryAll();

        // then:
        assertThat(result, contains(button1));
    }

}
