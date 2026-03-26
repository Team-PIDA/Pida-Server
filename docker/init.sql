CREATE EXTENSION IF NOT EXISTS postgis;

-- Spatial GIST index for bbox queries (ST_Contains)
CREATE INDEX IF NOT EXISTS idx_flower_spot_pin_point_gist
    ON t_flower_spot USING GIST (pin_point)
    WHERE deleted_at IS NULL;

-- Spatial GIST index for radius queries (ST_DWithin)
CREATE INDEX IF NOT EXISTS idx_flower_spot_pin_point_geog_gist
    ON t_flower_spot USING GIST ((pin_point::geography))
    WHERE deleted_at IS NULL;