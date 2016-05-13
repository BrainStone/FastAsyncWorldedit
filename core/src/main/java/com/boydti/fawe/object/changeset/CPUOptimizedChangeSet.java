package com.boydti.fawe.object.changeset;

import com.boydti.fawe.object.FaweChunk;
import com.boydti.fawe.object.RunnableVal2;
import com.boydti.fawe.object.change.MutableChunkChange;
import com.boydti.fawe.util.FaweQueue;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.history.change.Change;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.Iterator;

public class CPUOptimizedChangeSet extends FaweChangeSet {

    public CPUOptimizedChangeSet(World world) {
        super(world);
    }

    private ArrayList<Change> changes = new ArrayList<>();

    public void addChangeTask(FaweQueue queue) {
        queue.setChangeTask(new RunnableVal2<FaweChunk, FaweChunk>() {
            @Override
            public void run(final FaweChunk previous, final FaweChunk next) {
                char[][] previousIds = previous.getCombinedIdArrays();
                char[][] nextIds = next.getCombinedIdArrays();
                for (int i = 0; i < nextIds.length; i++) {
                    if (nextIds[i] != null && previousIds[i] == null) {
                        previous.fillCuboid(0, 15, i << 4, (i << 4) + 15, 0, 15, 0, (byte) 0);
                    }
                }
                changes.add(new MutableChunkChange(previous, next));
            }
        });
    }

    @Override

    public void add(int x, int y, int z, int combinedFrom, int combinedTo) {
        throw new UnsupportedOperationException("Invalid mode");
    }

    @Override
    public void addTileCreate(CompoundTag tag) {
        throw new UnsupportedOperationException("Invalid mode");
    }

    @Override
    public void addTileRemove(CompoundTag tag) {
        throw new UnsupportedOperationException("Invalid mode");
    }

    @Override
    public void addEntityRemove(CompoundTag tag) {
        throw new UnsupportedOperationException("Invalid mode");
    }

    @Override
    public void addEntityCreate(CompoundTag tag) {
        throw new UnsupportedOperationException("Invalid mode");
    }

    @Override
    public Iterator<Change> getIterator(boolean redo) {
        return changes.iterator();
    }

    @Override
    public int size() {
        return changes.size() * 65536; // num chunks * 65536 (guess of 65536 changes per chunk)
    }
}