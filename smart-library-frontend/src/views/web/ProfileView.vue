<template>
  <div class="min-h-screen bg-canvas py-12 px-4">
    <div class="max-w-4xl mx-auto">
      <!-- 页面标题 -->
      <h1 class="text-3xl font-bold text-ink mb-8">个人中心</h1>

      <!-- 用户信息卡片 -->
      <div class="bg-white rounded-2xl shadow-sm p-8 mb-6">
        <div class="flex items-start gap-6">
          <!-- 头像 -->
          <div class="flex-shrink-0">
            <div v-if="user.avatarUrl" class="w-24 h-24 rounded-full overflow-hidden">
              <img :src="user.avatarUrl" :alt="user.username" class="w-full h-full object-cover" />
            </div>
            <div v-else class="w-24 h-24 rounded-full bg-structure flex items-center justify-center text-3xl text-ink-light">
              {{ user.username?.charAt(0) || '用' }}
            </div>
          </div>

          <!-- 用户信息 -->
          <div class="flex-1">
            <h2 class="text-2xl font-bold text-ink mb-2">{{ user.username }}</h2>
            
            <div class="flex gap-4 text-sm text-ink-light mb-4">
              <div>
                <span class="font-medium">注册时间：</span>
                {{ formatDate(user.ctime) }}
              </div>
            </div>

            <!-- 个人简介 -->
            <div v-if="user.bio" class="text-sm text-ink-light">
              {{ user.bio }}
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-2">
            <button
              @click="isEditing = true"
              class="px-4 py-2 bg-pop text-white rounded-lg hover:opacity-90 transition-opacity"
            >
              编辑资料
            </button>
            <button
              @click="isViewingSecurity = true"
              class="px-4 py-2 bg-ink text-white rounded-lg hover:opacity-90 transition-opacity"
            >
              账号安全
            </button>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div 
          @click="$router.push('/favorites')" 
          class="bg-white rounded-xl shadow-sm p-6 text-center cursor-pointer"
        >
          <div class="text-3xl font-bold text-pop mb-2">{{ stats.favoriteCount }}</div>
          <div class="text-ink-light">我的收藏</div>
        </div>
        <div 
          @click="$router.push('/history')" 
          class="bg-white rounded-xl shadow-sm p-6 text-center cursor-pointer"
        >
          <div class="text-3xl font-bold text-pop mb-2">{{ stats.historyCount }}</div>
          <div class="text-ink-light">浏览历史</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6 text-center">
          <div class="text-3xl font-bold text-pop mb-2">{{ stats.commentCount || 0 }}</div>
          <div class="text-ink-light">我的评论</div>
        </div>
      </div>
    </div>

    <!-- 账号安全弹窗 - 简约现代风格 -->
    <a-modal
      v-model:visible="isViewingSecurity"
      :footer="false"
      width="540px"
      :body-style="{ padding: 0 }"
      :modal-style="{ borderRadius: '16px', overflow: 'hidden' }"
      :closable="false"
    >
      <div class="bg-white">
        <!-- 标题栏 -->
        <div class="px-8 py-6 border-b border-structure/30 flex items-start justify-between">
          <div>
            <h2 class="text-xl font-serif font-semibold text-ink">账号安全</h2>
            <p class="text-xs text-ink-light mt-1">管理你的登录信息和安全设置</p>
          </div>
          <button
            @click="isViewingSecurity = false"
            class="flex-shrink-0 w-8 h-8 flex items-center justify-center rounded-lg text-ink-light hover:text-ink hover:bg-canvas transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 安全项列表 -->
        <div class="p-6">
          <div class="space-y-3">
            <!-- 登录密码 -->
            <div class="flex items-center justify-between p-4 rounded-xl bg-canvas/50 hover:bg-canvas transition-colors group">
              <div class="flex items-center gap-4 flex-1">
                <div class="w-10 h-10 rounded-full bg-white flex items-center justify-center flex-shrink-0">
                  <svg class="w-5 h-5 text-ink" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                  </svg>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-medium text-ink">登录密码</div>
                  <div class="text-xs text-ink-light mt-0.5">定期更换密码可以提高账号安全性</div>
                </div>
              </div>
              <button
                @click="isChangingPassword = true; isViewingSecurity = false"
                class="px-4 py-2 text-sm font-medium text-pop hover:bg-white rounded-lg transition-colors"
              >
                修改
              </button>
            </div>

            <!-- 手机号 -->
            <div class="flex items-center justify-between p-4 rounded-xl bg-canvas/50 hover:bg-canvas transition-colors group">
              <div class="flex items-center gap-4 flex-1">
                <div class="w-10 h-10 rounded-full bg-white flex items-center justify-center flex-shrink-0">
                  <svg class="w-5 h-5 text-ink" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" />
                  </svg>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-medium text-ink">手机号</div>
                  <div class="text-xs text-ink-light mt-0.5">{{ maskPhone(user.phone) }}</div>
                </div>
              </div>
              <button
                @click="isChangingPhone = true; isViewingSecurity = false"
                class="px-4 py-2 text-sm font-medium text-pop hover:bg-white rounded-lg transition-colors"
              >
                修改
              </button>
            </div>

            <!-- 邮箱 -->
            <div class="flex items-center justify-between p-4 rounded-xl bg-canvas/50 hover:bg-canvas transition-colors group">
              <div class="flex items-center gap-4 flex-1">
                <div class="w-10 h-10 rounded-full bg-white flex items-center justify-center flex-shrink-0">
                  <svg class="w-5 h-5 text-ink" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                  </svg>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-medium text-ink">邮箱地址</div>
                  <div class="text-xs text-ink-light mt-0.5">{{ maskEmail(user.email) }}</div>
                </div>
              </div>
              <button
                @click="isChangingEmail = true; isViewingSecurity = false"
                class="px-4 py-2 text-sm font-medium text-pop hover:bg-white rounded-lg transition-colors"
              >
                修改
              </button>
            </div>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- 编辑资料弹窗 - 简约现代风格 -->
    <a-modal
      v-model:visible="isEditing"
      :footer="false"
      width="540px"
      :body-style="{ padding: 0 }"
      :modal-style="{ borderRadius: '16px', overflow: 'hidden' }"
      :closable="false"
    >
      <div class="bg-white">
        <!-- 标题栏 -->
        <div class="px-8 py-6 border-b border-structure/30 flex items-start justify-between">
          <div>
            <h2 class="text-xl font-serif font-semibold text-ink">编辑资料</h2>
            <p class="text-xs text-ink-light mt-1">更新你的个人信息</p>
          </div>
          <button
            @click="handleCancel"
            class="flex-shrink-0 w-8 h-8 flex items-center justify-center rounded-lg text-ink-light hover:text-ink hover:bg-canvas transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 表单内容 -->
        <div class="px-8 py-6 space-y-6">
          <!-- 头像上传 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-3">头像</label>
            <div class="flex items-center gap-6">
              <!-- 头像预览 -->
              <div class="relative group">
                <div v-if="editForm.avatarUrl" class="w-24 h-24 rounded-full overflow-hidden ring-2 ring-structure/50">
                  <img :src="editForm.avatarUrl" alt="头像预览" class="w-full h-full object-cover" />
                </div>
                <div v-else class="w-24 h-24 rounded-full bg-canvas flex items-center justify-center text-3xl text-ink-light ring-2 ring-structure/50">
                  {{ editForm.username?.charAt(0) || '用' }}
                </div>
                <!-- 上传遮罩 -->
                <div v-if="uploadingAvatar" class="absolute inset-0 bg-ink/50 rounded-full flex items-center justify-center">
                  <svg class="animate-spin h-6 w-6 text-white" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                </div>
              </div>

              <!-- 上传按钮 -->
              <div class="flex-1">
                <a-upload
                  :custom-request="handleAvatarUpload"
                  :show-file-list="false"
                  accept="image/*"
                  :disabled="uploadingAvatar"
                >
                  <button
                    type="button"
                    :disabled="uploadingAvatar"
                    class="px-5 py-2.5 bg-ink text-white text-sm font-medium rounded-lg hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                  >
                    <svg v-if="uploadingAvatar" class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <span>{{ uploadingAvatar ? '上传中...' : '选择头像' }}</span>
                  </button>
                </a-upload>
                <p class="text-xs text-ink-light mt-2">支持 JPG、PNG 格式，不超过 2MB</p>
              </div>
            </div>
          </div>

          <!-- 用户名 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">用户名</label>
            <input
              v-model="editForm.username"
              type="text"
              placeholder="请输入用户名"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all"
            />
          </div>

          <!-- 个人简介 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">个人简介</label>
            <textarea
              v-model="editForm.bio"
              placeholder="介绍一下自己吧..."
              rows="4"
              maxlength="200"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all resize-none"
            ></textarea>
            <div class="flex justify-end mt-1">
              <span class="text-xs text-ink-light">{{ editForm.bio?.length || 0 }} / 200</span>
            </div>
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="px-8 py-5 bg-canvas/30 border-t border-structure/30 flex items-center justify-end gap-3">
          <button
            type="button"
            @click="handleSave"
            :disabled="saving"
            class="px-6 py-2.5 bg-ink text-white text-sm font-medium rounded-lg hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            <svg v-if="saving" class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>{{ saving ? '保存中...' : '保存' }}</span>
          </button>
        </div>
      </div>
    </a-modal>

    <!-- 修改密码弹窗 - 简约现代风格 -->
    <a-modal
      v-model:visible="isChangingPassword"
      :footer="false"
      width="540px"
      :body-style="{ padding: 0 }"
      :modal-style="{ borderRadius: '16px', overflow: 'hidden' }"
      :closable="false"
    >
      <div class="bg-white">
        <!-- 标题栏 -->
        <div class="px-8 py-6 border-b border-structure/30 flex items-start justify-between">
          <div>
            <h2 class="text-xl font-serif font-semibold text-ink">修改密码</h2>
            <p class="text-xs text-ink-light mt-1">定期更换密码可以提高账号安全性</p>
          </div>
          <button
            @click="handleCancelPassword"
            class="flex-shrink-0 w-8 h-8 flex items-center justify-center rounded-lg text-ink-light hover:text-ink hover:bg-canvas transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 表单内容 -->
        <div class="px-8 py-6 space-y-5">
          <!-- 旧密码 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">旧密码</label>
            <input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入旧密码"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all"
            />
          </div>

          <!-- 新密码 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">新密码</label>
            <input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码（6-20位）"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all"
            />
          </div>

          <!-- 确认新密码 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">确认新密码</label>
            <input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all"
            />
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="px-8 py-5 bg-canvas/30 border-t border-structure/30 flex items-center justify-end gap-3">
          <button
            type="button"
            @click="handleChangePassword"
            :disabled="changingPassword"
            class="px-6 py-2.5 bg-pop text-white text-sm font-medium rounded-lg hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            <svg v-if="changingPassword" class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>{{ changingPassword ? '修改中...' : '确认修改' }}</span>
          </button>
        </div>
      </div>
    </a-modal>

    <!-- 修改手机号弹窗 - 简约现代风格 -->
    <a-modal
      v-model:visible="isChangingPhone"
      :footer="false"
      width="540px"
      :body-style="{ padding: 0 }"
      :modal-style="{ borderRadius: '16px', overflow: 'hidden' }"
      :closable="false"
    >
      <div class="bg-white">
        <!-- 标题栏 -->
        <div class="px-8 py-6 border-b border-structure/30 flex items-start justify-between">
          <div>
            <h2 class="text-xl font-serif font-semibold text-ink">修改手机号</h2>
            <p class="text-xs text-ink-light mt-1">更换绑定的手机号码</p>
          </div>
          <button
            @click="handleCancelPhone"
            class="flex-shrink-0 w-8 h-8 flex items-center justify-center rounded-lg text-ink-light hover:text-ink hover:bg-canvas transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 表单内容 -->
        <div class="px-8 py-6 space-y-5">
          <!-- 当前手机号 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">当前手机号</label>
            <input
              v-model="phoneForm.oldPhone"
              type="tel"
              placeholder="请输入当前手机号"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all"
            />
          </div>

          <!-- 新手机号 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">新手机号</label>
            <input
              v-model="phoneForm.newPhone"
              type="tel"
              placeholder="请输入新手机号"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all"
            />
          </div>

          <!-- 密码确认 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">密码确认</label>
            <input
              v-model="phoneForm.password"
              type="password"
              placeholder="请输入密码以确认修改"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all"
            />
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="px-8 py-5 bg-canvas/30 border-t border-structure/30 flex items-center justify-end gap-3">
          <button
            type="button"
            @click="handleChangePhone"
            :disabled="changingPhone"
            class="px-6 py-2.5 bg-pop text-white text-sm font-medium rounded-lg hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            <svg v-if="changingPhone" class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>{{ changingPhone ? '修改中...' : '确认修改' }}</span>
          </button>
        </div>
      </div>
    </a-modal>

    <!-- 修改邮箱弹窗 - 简约现代风格 -->
    <a-modal
      v-model:visible="isChangingEmail"
      :footer="false"
      width="540px"
      :body-style="{ padding: 0 }"
      :modal-style="{ borderRadius: '16px', overflow: 'hidden' }"
      :closable="false"
    >
      <div class="bg-white">
        <!-- 标题栏 -->
        <div class="px-8 py-6 border-b border-structure/30 flex items-start justify-between">
          <div>
            <h2 class="text-xl font-serif font-semibold text-ink">修改邮箱</h2>
            <p class="text-xs text-ink-light mt-1">更换绑定的邮箱地址</p>
          </div>
          <button
            @click="handleCancelEmail"
            class="flex-shrink-0 w-8 h-8 flex items-center justify-center rounded-lg text-ink-light hover:text-ink hover:bg-canvas transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 表单内容 -->
        <div class="px-8 py-6 space-y-5">
          <!-- 当前邮箱 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">当前邮箱</label>
            <input
              v-model="emailForm.oldEmail"
              type="email"
              placeholder="请输入当前邮箱"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all"
            />
          </div>

          <!-- 新邮箱 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">新邮箱</label>
            <input
              v-model="emailForm.newEmail"
              type="email"
              placeholder="请输入新邮箱"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all"
            />
          </div>

          <!-- 密码确认 -->
          <div>
            <label class="block text-sm font-medium text-ink mb-2">密码确认</label>
            <input
              v-model="emailForm.password"
              type="password"
              placeholder="请输入密码以确认修改"
              class="w-full px-4 py-3 bg-canvas border-0 rounded-lg text-ink placeholder:text-ink-light/50 focus:outline-none focus:ring-2 focus:ring-ink/20 transition-all"
            />
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="px-8 py-5 bg-canvas/30 border-t border-structure/30 flex items-center justify-end gap-3">
          <button
            type="button"
            @click="handleChangeEmail"
            :disabled="changingEmail"
            class="px-6 py-2.5 bg-pop text-white text-sm font-medium rounded-lg hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            <svg v-if="changingEmail" class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>{{ changingEmail ? '修改中...' : '确认修改' }}</span>
          </button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getProfile, updateProfile, countUserComments, changePassword, changePhone, changeEmail } from '@/api/user'
import { countUserFavorites } from '@/api/favorite'
import { countBrowseHistory } from '@/api/history'
import { uploadFile } from '@/api/minio'
import { Message } from '@arco-design/web-vue'

const authStore = useAuthStore()

// 用户信息
const user = ref({})

// 统计数据
const stats = reactive({
  favoriteCount: 0,
  historyCount: 0,
  commentCount: 0
})

// 编辑状态
const isEditing = ref(false)
const saving = ref(false)
const uploadingAvatar = ref(false)

// 账号安全弹窗状态
const isViewingSecurity = ref(false)

// 修改密码状态
const isChangingPassword = ref(false)
const changingPassword = ref(false)

// 修改手机号状态
const isChangingPhone = ref(false)
const changingPhone = ref(false)

// 修改邮箱状态
const isChangingEmail = ref(false)
const changingEmail = ref(false)

// 编辑表单（移除手机号和邮箱）
const editForm = reactive({
  username: '',
  bio: '',
  avatarUrl: ''
})

// 修改密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 修改手机号表单
const phoneForm = reactive({
  oldPhone: '',
  newPhone: '',
  password: ''
})

// 修改邮箱表单
const emailForm = reactive({
  oldEmail: '',
  newEmail: '',
  password: ''
})

/**
 * 格式化日期
 */
const formatDate = (dateStr) => {
  if (!dateStr) return '未知'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

/**
 * 脱敏手机号（保留前3位和后4位）
 */
const maskPhone = (phone) => {
  if (!phone) return '未设置'
  if (phone.length === 11) {
    return phone.substring(0, 3) + '****' + phone.substring(7)
  }
  return phone
}

/**
 * 脱敏邮箱（保留前2位和@后的域名）
 */
const maskEmail = (email) => {
  if (!email) return '未设置'
  const atIndex = email.indexOf('@')
  if (atIndex > 2) {
    const prefix = email.substring(0, 2)
    const domain = email.substring(atIndex)
    return prefix + '***' + domain
  }
  return email
}

/**
 * 加载用户信息
 */
const loadUserProfile = async () => {
  try {
    const res = await getProfile()
    
    if (res.code === 0) {
      user.value = res.data
      
      // 初始化编辑表单（只包含用户名、简介、头像）
      editForm.username = res.data.username
      editForm.bio = res.data.bio || ''
      editForm.avatarUrl = res.data.avatarUrl || ''
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    Message.error('加载用户信息失败')
  }
}

/**
 * 处理头像上传
 */
const handleAvatarUpload = async (options) => {
  // Arco Design 的 custom-request 中，fileItem.file 才是真正的 File 对象
  const file = options.fileItem.file
  
  if (!file) {
    Message.error('无法获取文件')
    return
  }
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    Message.error('请上传图片文件')
    return
  }
  
  // 验证文件大小（限制2MB）
  if (file.size > 2 * 1024 * 1024) {
    Message.error('图片大小不能超过2MB')
    return
  }
  
  uploadingAvatar.value = true
  
  try {
    // 创建本地预览 URL
    const localUrl = URL.createObjectURL(file)
    editForm.avatarUrl = localUrl
    
    // 上传到服务器
    const formData = new FormData()
    formData.append('file', file)
    formData.append('bucketName', 'avatar')
    
    const res = await uploadFile(formData)
    
    // 释放本地 URL
    URL.revokeObjectURL(localUrl)
    
    if (res.code === 0) {
      // 上传成功后使用服务器返回的 URL
      editForm.avatarUrl = res.data
      Message.success('头像上传成功')
    } else {
      Message.error(res.message || '头像上传失败')
      // 恢复原头像
      editForm.avatarUrl = user.value.avatarUrl || ''
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    Message.error('头像上传失败')
    // 恢复原头像
    editForm.avatarUrl = user.value.avatarUrl || ''
  } finally {
    uploadingAvatar.value = false
  }
}

/**
 * 加载统计数据
 */
const loadStats = async () => {
  try {
    // 加载收藏数量
    const favoriteRes = await countUserFavorites()
    if (favoriteRes.code === 0) {
      stats.favoriteCount = favoriteRes.data
    }

    // 加载浏览历史数量
    const historyRes = await countBrowseHistory()
    if (historyRes.code === 0) {
      stats.historyCount = historyRes.data
    }

    // 加载评论数量
    const commentRes = await countUserComments()
    if (commentRes.code === 0) {
      stats.commentCount = commentRes.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

/**
 * 保存编辑
 */
const handleSave = async () => {
  saving.value = true
  try {
    const res = await updateProfile(editForm)
    
    if (res.code === 0) {
      Message.success('保存成功')
      isEditing.value = false
      
      // 重新加载用户信息
      await loadUserProfile()
      
      // 更新 store 中的用户信息
      authStore.updateUser({
        username: editForm.username,
        bio: editForm.bio,
        avatarUrl: editForm.avatarUrl
      })
    } else {
      Message.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    Message.error('保存失败')
  } finally {
    saving.value = false
  }
}

/**
 * 取消编辑
 */
const handleCancel = () => {
  // 恢复原始数据
  editForm.username = user.value.username
  editForm.bio = user.value.bio || ''
  editForm.avatarUrl = user.value.avatarUrl || ''
  isEditing.value = false
}

/**
 * 修改密码
 */
const handleChangePassword = async () => {
  // 验证表单
  if (!passwordForm.oldPassword) {
    Message.error('请输入旧密码')
    return
  }
  if (!passwordForm.newPassword) {
    Message.error('请输入新密码')
    return
  }
  if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 20) {
    Message.error('新密码长度应为6-20位')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    Message.error('两次输入的密码不一致')
    return
  }
  if (passwordForm.oldPassword === passwordForm.newPassword) {
    Message.error('新密码不能与旧密码相同')
    return
  }

  changingPassword.value = true
  try {
    const res = await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    if (res.code === 0) {
      Message.success('密码修改成功，请重新登录')
      isChangingPassword.value = false
      // 清空表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      // 退出登录
      setTimeout(() => {
        authStore.logout()
      }, 1500)
    } else {
      Message.error(res.message || '密码修改失败')
    }
  } catch (error) {
    console.error('密码修改失败:', error)
    Message.error('密码修改失败')
  } finally {
    changingPassword.value = false
  }
}

/**
 * 取消修改密码
 */
const handleCancelPassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  isChangingPassword.value = false
}

/**
 * 修改手机号
 */
const handleChangePhone = async () => {
  // 验证表单
  if (!phoneForm.oldPhone) {
    Message.error('请输入当前手机号')
    return
  }
  if (!phoneForm.newPhone) {
    Message.error('请输入新手机号')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(phoneForm.newPhone)) {
    Message.error('请输入正确的手机号格式')
    return
  }
  if (!phoneForm.password) {
    Message.error('请输入密码以确认修改')
    return
  }
  if (phoneForm.oldPhone === phoneForm.newPhone) {
    Message.error('新手机号不能与当前手机号相同')
    return
  }

  changingPhone.value = true
  try {
    const res = await changePhone({
      oldPhone: phoneForm.oldPhone,
      newPhone: phoneForm.newPhone,
      password: phoneForm.password
    })
    if (res.code === 0) {
      Message.success('手机号修改成功')
      isChangingPhone.value = false
      // 清空表单
      phoneForm.oldPhone = ''
      phoneForm.newPhone = ''
      phoneForm.password = ''
      // 重新加载用户信息
      await loadUserProfile()
    } else {
      Message.error(res.message || '手机号修改失败')
    }
  } catch (error) {
    console.error('手机号修改失败:', error)
    Message.error('手机号修改失败')
  } finally {
    changingPhone.value = false
  }
}

/**
 * 取消修改手机号
 */
const handleCancelPhone = () => {
  phoneForm.oldPhone = ''
  phoneForm.newPhone = ''
  phoneForm.password = ''
  isChangingPhone.value = false
}

/**
 * 修改邮箱
 */
const handleChangeEmail = async () => {
  // 验证表单
  if (!emailForm.oldEmail) {
    Message.error('请输入当前邮箱')
    return
  }
  if (!emailForm.newEmail) {
    Message.error('请输入新邮箱')
    return
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.newEmail)) {
    Message.error('请输入正确的邮箱格式')
    return
  }
  if (!emailForm.password) {
    Message.error('请输入密码以确认修改')
    return
  }
  if (emailForm.oldEmail === emailForm.newEmail) {
    Message.error('新邮箱不能与当前邮箱相同')
    return
  }

  changingEmail.value = true
  try {
    const res = await changeEmail({
      oldEmail: emailForm.oldEmail,
      newEmail: emailForm.newEmail,
      password: emailForm.password
    })
    if (res.code === 0) {
      Message.success('邮箱修改成功')
      isChangingEmail.value = false
      // 清空表单
      emailForm.oldEmail = ''
      emailForm.newEmail = ''
      emailForm.password = ''
      // 重新加载用户信息
      await loadUserProfile()
    } else {
      Message.error(res.message || '邮箱修改失败')
    }
  } catch (error) {
    console.error('邮箱修改失败:', error)
    Message.error('邮箱修改失败')
  } finally {
    changingEmail.value = false
  }
}

/**
 * 取消修改邮箱
 */
const handleCancelEmail = () => {
  emailForm.oldEmail = ''
  emailForm.newEmail = ''
  emailForm.password = ''
  isChangingEmail.value = false
}

onMounted(() => {
  loadUserProfile()
  loadStats()
})
</script>

<style scoped>
/* 强制覆盖 Arco Design Upload 组件的默认按钮样式 */
:deep(.arco-upload) {
  display: inline-block;
}

:deep(.arco-upload-trigger) {
  display: inline-block;
}

/* 强制覆盖按钮的背景色和文字色 */
:deep(.arco-upload button) {
  background-color: #102a43 !important;
  color: white !important;
  border: none !important;
}

:deep(.arco-upload button:hover:not(:disabled)) {
  background-color: #102a43 !important;
  opacity: 0.9 !important;
}

:deep(.arco-upload button:disabled) {
  background-color: #102a43 !important;
  opacity: 0.5 !important;
}
</style>
