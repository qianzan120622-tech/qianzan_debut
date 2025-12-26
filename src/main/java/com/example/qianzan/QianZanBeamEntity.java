package com.example.qianzan;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class QianZanBeamEntity extends ThrowableItemProjectile {
    private boolean isOrbiting = false;
    private float orbitOffset = 0f;
    private int maxLifeTime = 200;

    // 基础构造函数
    public QianZanBeamEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    // 发射用构造函数
    public QianZanBeamEntity(Level level, LivingEntity shooter) {
        super(QianZan.QIANZAN_BEAM.get(), shooter, level);
        this.setNoGravity(true);
    }

    // 开启环绕模式
    public void setOrbit(float offset) {
        this.isOrbiting = true;
        this.orbitOffset = offset;
        this.setNoGravity(true);
        this.maxLifeTime = 100; // 5秒
    }

    @Override
    protected Item getDefaultItem() {
        // 返回新的剑气贴图物品
        return QianZan.QIANZAN_BEAM_SPRITE.get();
    }

    @Override
    public void tick() {
        super.tick();

        // 寿命检查
        if (!this.level().isClientSide && this.tickCount > maxLifeTime) {
            this.discard();
            return;
        }

        if (this.isOrbiting) {
            Entity owner = this.getOwner();
            if (owner != null && owner.isAlive()) {
                // 环绕逻辑
                double radius = 2.0;
                double speed = 0.1;
                double angle = (this.tickCount * speed) + orbitOffset;

                double x = owner.getX() + Math.cos(angle) * radius;
                double y = owner.getY() + 1.0;
                double z = owner.getZ() + Math.sin(angle) * radius;

                this.setPos(x, y, z);
                this.setDeltaMovement(Vec3.ZERO);
                this.lookAt(EntityAnchorArgument.Anchor.EYES, owner.getEyePosition()); // 始终朝向主人

                // 手动碰撞检测
                if (!this.level().isClientSide) {
                    AABB detectionBox = this.getBoundingBox().inflate(0.2);
                    List<LivingEntity> targets = this.level().getEntitiesOfClass(
                            LivingEntity.class, detectionBox,
                            e -> e != owner && e.isAlive()
                    );
                    for (LivingEntity target : targets) {
                        hitTarget(target);
                        this.discard();
                        break;
                    }
                }
            } else {
                if (!this.level().isClientSide) this.discard();
            }
        } else {
            // R键发射的剑气保持无重力
            this.setNoGravity(true);
        }

        // 粒子效果
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide && result.getEntity() instanceof LivingEntity target) {
            hitTarget(target);
        }
    }

    // 统一的伤害处理方法
    private void hitTarget(LivingEntity target) {
        float damage = 10.0f; // 伤害值固定为 10
        target.hurt(this.damageSources().thrown(this, this.getOwner()), damage);
    }
}