export default [
    {
        path: "/goods/list",
        component: () => import('~/addon/mall/pages/goods/list.vue')
    },
    {
        path: "/goods/detail",
        component: () => import('~/addon/mall/pages/goods/detail.vue')
    },
    {
        path: "/goods/cart",
        component: () => import('~/addon/mall/pages/goods/cart.vue')
    },
    {
        path: "/shop/list",
        component: () => import('~/addon/mall/pages/shop/list.vue')
    },
    {
        path: "/shop/index",
        component: () => import('~/addon/mall/pages/shop/index.vue'),
        meta: {
            layout: "shop"
        }
    },
    {
        path: "/shop/detail",
        component: () => import('~/addon/mall/pages/shop/detail.vue'),
        meta: {
            layout: "shop"
        }
    },
    {
        path: "/shop/coupon",
        component: () => import('~/addon/mall/pages/shop/coupon.vue'),
        meta: {
            layout: "shop"
        }
    },
    {
        path: "/order/payment",
        component: () => import('~/addon/mall/pages/order/payment.vue'),
        meta: {
            middleware: ["auth"]
        }
    },
    {
        path: "/order/list",
        component: () => import('~/addon/mall/pages/order/list.vue'),
        meta: {
            middleware: ["auth"],
            layout: "member"
        }
    },
    {
        path: "/order/detail",
        component: () => import('~/addon/mall/pages/order/detail.vue'),
        meta: {
            middleware: ["auth"]
        }
    },
    {
        path: "/refund/list",
        component: () => import('~/addon/mall/pages/refund/list.vue'),
        meta: {
            middleware: ["auth"],
            layout: "member"
        }
    },
    {
        path: "/refund/detail",
        component: () => import('~/addon/mall/pages/refund/detail.vue'),
        meta: {
            middleware: ["auth"]
        }
    },
    {
        path: "/refund/apply",
        component: () => import('~/addon/mall/pages/refund/apply.vue'),
        meta: {
            middleware: ["auth"]
        }
    },
    {
        path: "/coupon/list",
        component: () => import('~/addon/mall/pages/coupon/list.vue')
    },
    {
        path: "/member/my_coupon",
        component: () => import('~/addon/mall/pages/member/my_coupon.vue'),
        meta: {
            middleware: ["auth"],
             layout: "member"
        }
    },
    {
        path: "/member/merchant_settled",
        component: () => import('~/addon/mall/pages/member/merchant_settled.vue'),
        meta: {
            middleware: ["auth"]
        }
    },
    {
        path: "/member/merchant_record",
        component: () => import('~/addon/mall/pages/member/merchant_record.vue'),
        meta: {
            middleware: ["auth"]
        }
    },
    {
        path: "/member/address_list",
        component: () => import('~/addon/mall/pages/member/address_list.vue'),
        meta: {
            middleware: ["auth"],
            layout: "member"
        }
    },
    {
        path: "/member/browse",
        component: () => import('~/addon/mall/pages/member/browse.vue'),
        meta: {
            middleware: ["auth"],
            layout: "member"
        }
    },

]
