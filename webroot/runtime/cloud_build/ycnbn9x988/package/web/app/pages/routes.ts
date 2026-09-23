export default [
    {
        path: "/",
        component: () => import('~/app/pages/index.vue')
    },
    {
        path: "/auth/login",
        component: () => import('~/app/pages/auth/login.vue'),
        meta: {
            layout: "container"
        }
    },
    {
        path: "/auth/register",
        component: () => import('~/app/pages/auth/register.vue'),
        meta: {
            layout: "container"
        }
    },
    {
        path: "/auth/bind",
        component: () => import('~/app/pages/auth/bind.vue'),
        meta: {
            layout: "container"
        }
    },
    {
        path: "/auth/agreement",
        component: () => import('~/app/pages/auth/agreement.vue'),
        meta: {
            layout: "member"
        }
    },
    {
        path: "/member/collect/goods",
        component: () => import('~/app/pages/member/collect_goods.vue'),
        meta: {
            middleware: ["auth"],
            layout: "member"
        }
    },
    {
        path: "/member/collect/shop",
        component: () => import('~/app/pages/member/collect_shop.vue'),
        meta: {
            middleware: ["auth"],
            layout: "member"
        }
    },
    {
        path: "/member/center",
        component: () => import('~/app/pages/member/center.vue'),
        meta: {
            middleware: ["auth"],
            layout: "member"
        }
    },
    {
        path: "/member/balance",
        component: () => import('~/app/pages/member/balance.vue'),
        meta: {
            middleware: ["auth"],
            layout: "member"
        }
        
    },
    {
        path: "/member/point",
        component: () => import('~/app/pages/member/point.vue'),
        meta: {
            middleware: ["auth"],
            layout: "member"
        }
    },
    {
        path: "/pay/pay",
        component: () => import('~/app/pages/pay/pay.vue'),
        meta: {
            middleware: ["auth"]
        }
    },
    {
        path: "/pay/pay_succeed",
        component: () => import('~/app/pages/pay/pay_succeed.vue'),
        meta: {
            middleware: ["auth"]
        }
    },
    {
        path: "/site/close",
        component: () => import('~/app/pages/site/close.vue'),
        meta: {
            layout: "container"
        }
    },
    {
        path: "/site/nosite",
        component: () => import('~/app/pages/site/nosite.vue'),
        meta: {
            layout: "container"
        }
    },
    { 
        path: "/site/store_close",
        component: () => import('~/app/pages/site/store_close.vue'),
        meta: {
            layout: "container"
        }
    },
    {
        path: "/article/list",
        component: () => import('~/app/pages/article/list.vue')
    },
    {
        path: "/article/detail",
        component: () => import('~/app/pages/article/detail.vue')
    }
]
