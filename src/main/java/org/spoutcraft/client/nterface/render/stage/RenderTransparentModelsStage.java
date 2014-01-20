package org.spoutcraft.client.nterface.render.stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spout.renderer.api.Creatable;
import org.spout.renderer.api.Material;
import org.spout.renderer.api.Pipeline;
import org.spout.renderer.api.Pipeline.PipelineBuilder;
import org.spout.renderer.api.data.Uniform.Matrix4Uniform;
import org.spout.renderer.api.gl.Context.BlendFunction;
import org.spout.renderer.api.gl.Context.Capability;
import org.spout.renderer.api.gl.FrameBuffer;
import org.spout.renderer.api.gl.FrameBuffer.AttachmentPoint;
import org.spout.renderer.api.gl.GLFactory;
import org.spout.renderer.api.gl.Texture;
import org.spout.renderer.api.gl.Texture.Format;
import org.spout.renderer.api.gl.Texture.InternalFormat;
import org.spout.renderer.api.model.Model;

import org.spoutcraft.client.nterface.render.Renderer;

/**
 *
 */
public class RenderTransparentModelsStage extends Creatable {
    private final Renderer renderer;
    private final Material material;
    private final Texture weightedColors;
    private final Texture weightedVelocities;
    private final Texture layerCounts;
    private final FrameBuffer weightedSumFrameBuffer;
    private final FrameBuffer frameBuffer;
    private Texture depthsInput;
    private Texture colorsOutput;
    private Texture velocitiesOutput;
    private final List<Model> models = new ArrayList<>();
    private Pipeline pipeline;

    public RenderTransparentModelsStage(Renderer renderer) {
        this.renderer = renderer;
        material = new Material(renderer.getProgram("transparencyBlending"));
        final GLFactory glFactory = renderer.getGLFactory();
        weightedColors = glFactory.createTexture();
        weightedVelocities = glFactory.createTexture();
        layerCounts = glFactory.createTexture();
        weightedSumFrameBuffer = glFactory.createFrameBuffer();
        frameBuffer = glFactory.createFrameBuffer();
    }

    @Override
    public void create() {
        if (isCreated()) {
            throw new IllegalStateException("Render transparent models stage has already been created");
        }
        // Create the weighted colors texture
        weightedColors.setFormat(Format.RGBA);
        weightedColors.setInternalFormat(InternalFormat.RGBA16F);
        weightedColors.setImageData(null, Renderer.WINDOW_SIZE.getFloorX(), Renderer.WINDOW_SIZE.getFloorY());
        weightedColors.create();
        // Create the weighted velocities texture
        weightedVelocities.setFormat(Format.RG);
        weightedVelocities.setInternalFormat(InternalFormat.RG16F);
        weightedVelocities.setImageData(null, Renderer.WINDOW_SIZE.getFloorX(), Renderer.WINDOW_SIZE.getFloorY());
        weightedVelocities.create();
        // Create the layer counts texture
        layerCounts.setFormat(Format.RED);
        layerCounts.setInternalFormat(InternalFormat.R16F);
        layerCounts.setImageData(null, Renderer.WINDOW_SIZE.getFloorX(), Renderer.WINDOW_SIZE.getFloorY());
        layerCounts.create();
        // Create the material
        material.addTexture(0, weightedColors);
        material.addTexture(1, weightedVelocities);
        material.addTexture(2, layerCounts);
        // Create the screen model
        final Model model = new Model(renderer.getScreen(), material);
        // Create the weighted sum frame buffer
        weightedSumFrameBuffer.attach(AttachmentPoint.COLOR0, weightedColors);
        weightedSumFrameBuffer.attach(AttachmentPoint.COLOR1, weightedVelocities);
        weightedSumFrameBuffer.attach(AttachmentPoint.COLOR2, layerCounts);
        weightedSumFrameBuffer.attach(AttachmentPoint.DEPTH, depthsInput);
        weightedSumFrameBuffer.create();
        // Create the frame buffer
        frameBuffer.attach(AttachmentPoint.COLOR0, colorsOutput);
        //frameBuffer.attach(AttachmentPoint.COLOR1, velocitiesOutput);
        frameBuffer.create();
        // Create the pipeline
        pipeline = new PipelineBuilder().disableDepthMask().disableCapabilities(Capability.CULL_FACE).enableCapabilities(Capability.BLEND)
                .setBlendingFunctions(BlendFunction.GL_ONE, BlendFunction.GL_ONE).bindFrameBuffer(weightedSumFrameBuffer).clearBuffer().renderModels(models)
                .enableCapabilities(Capability.CULL_FACE).enableDepthMask().setBlendingFunctions(BlendFunction.GL_ONE_MINUS_SRC_ALPHA, BlendFunction.GL_SRC_ALPHA)
                .bindFrameBuffer(frameBuffer).renderModels(Arrays.asList(model)).unbindFrameBuffer(frameBuffer).disableCapabilities(Capability.BLEND).enableDepthMask().build();
        // Update the state to created
        super.create();
    }

    @Override
    public void destroy() {
        checkCreated();
        weightedColors.destroy();
        weightedVelocities.destroy();
        layerCounts.destroy();
        weightedSumFrameBuffer.destroy();
        frameBuffer.destroy();
        if (colorsOutput.isCreated()) {
            colorsOutput.destroy();
        }
        //if (velocitiesOutput.isCreated()) {
        //    velocitiesOutput.destroy();
        //}
        super.destroy();
    }

    public void render() {
        checkCreated();
        pipeline.run(renderer.getContext());
    }

    public void setDepthsInput(Texture texture) {
        texture.checkCreated();
        depthsInput = texture;
    }

    public Texture getColorsOutput() {
        return colorsOutput;
    }

    public void setColorsOutput(Texture texture) {
        texture.checkCreated();
        colorsOutput = texture;
    }

    public Texture getVelocitiesOutput() {
        return velocitiesOutput;
    }

    public void setVelocitiesOutput(Texture texture) {
        texture.checkCreated();
        velocitiesOutput = texture;
    }

    /**
     * Adds a model to the renderer.
     *
     * @param model The model to add
     */
    public void addModel(Model model) {
        model.getUniforms().add(new Matrix4Uniform("previousModelMatrix", model.getMatrix()));
        models.add(model);
    }

    /**
     * Removes a model from the renderer.
     *
     * @param model The model to remove
     */
    public void removeModel(Model model) {
        models.remove(model);
    }

    /**
     * Removes all the models from the renderer.
     */
    public void clearModels() {
        models.clear();
    }

    public List<Model> getModels() {
        return models;
    }
}
