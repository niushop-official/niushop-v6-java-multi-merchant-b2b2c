const ADDON_INSTALL_RESTARTING_KEY = 'addon_install_restarting'

export function setAddonInstallRestarting(restarting: boolean) {
    if (restarting) {
        sessionStorage.setItem(ADDON_INSTALL_RESTARTING_KEY, '1')
    } else {
        sessionStorage.removeItem(ADDON_INSTALL_RESTARTING_KEY)
    }
}

export function isAddonInstallRestarting(): boolean {
    return sessionStorage.getItem(ADDON_INSTALL_RESTARTING_KEY) === '1'
}
