package cn.jzl.sect.ecs.sect

import cn.jzl.sect.ecs.MemberRole

/**
 * 权限类型定义
 * 控制宗门成员可以执行的操作
 */
sealed class Permission {
    /**
     * 管理宗门设置
     */
    data object ManageSect : Permission()

    /**
     * 管理宗门成员（添加、移除、晋升）
     */
    data object ManageMembers : Permission()

    /**
     * 管理宗门资源（存取）
     */
    data object ManageResources : Permission()

    /**
     * 发布任务
     */
    data object PublishTask : Permission()

    /**
     * 领取任务
     */
    data object AcceptTask : Permission()

    /**
     * 管理建筑（创建、升级）
     */
    data object ManageBuildings : Permission()

    /**
     * 管理功法（添加到藏经阁）
     */
    data object ManageTechniques : Permission()

    /**
     * 学习功法
     */
    data object LearnTechnique : Permission()

    /**
     * 领取福利
     */
    data object ClaimWelfare : Permission()

    /**
     * 发放福利
     */
    data object DistributeWelfare : Permission()

    companion object {
        /**
         * 获取指定角色的权限集合
         */
        fun getPermissionsFor(role: MemberRole): Set<Permission> = when (role) {
            MemberRole.LEADER -> setOf(
                ManageSect,
                ManageMembers,
                ManageResources,
                PublishTask,
                AcceptTask,
                ManageBuildings,
                ManageTechniques,
                LearnTechnique,
                ClaimWelfare,
                DistributeWelfare
            )
            MemberRole.ELDER -> setOf(
                ManageMembers,
                ManageResources,
                PublishTask,
                AcceptTask,
                ManageBuildings,
                ManageTechniques,
                LearnTechnique,
                ClaimWelfare,
                DistributeWelfare
            )
            MemberRole.INNER_DISCIPLE -> setOf(
                PublishTask,
                AcceptTask,
                LearnTechnique,
                ClaimWelfare
            )
            MemberRole.OUTER_DISCIPLE -> setOf(
                AcceptTask,
                LearnTechnique,
                ClaimWelfare
            )
        }

        /**
         * 检查指定角色是否有某权限
         */
        fun hasPermission(role: MemberRole, permission: Permission): Boolean =
            permission in getPermissionsFor(role)
    }
}

