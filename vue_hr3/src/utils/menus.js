import {getJsonRequest} from "@/utils/api";

/*后端请求菜单选项函数封装*/
export const initMenu=(router,store)=>{
    if (store.state.routes.length>0){
        return;
    }
    getJsonRequest("/system/config/menu").then(data=>{
        let fmtRoutes =formatRoutes(data.obj);
        router.addRoutes(fmtRoutes);
        store.commit('initRoutes',fmtRoutes)
    })
}

export const formatRoutes=(routes)=>{
    let fmRoutes=[];

    routes.forEach(route=>{
        let {
            path,
            name,
            meta,
            iconCls,
            children,
            component
        }=route;
        if (children && children instanceof Array){
            children=formatRoutes(children);
        }
        let fmRouter={
            path:path,
            name:name,
            meta:meta,
            iconCls:iconCls,
            children:children,
            component(resolve){
                /*截取path的路径 方便前往views目录寻找VUE组件*/
                let base='';
                let lastIndex=path.lastIndexOf('/');
                if (lastIndex!=0){
                    base=path.substring(1,lastIndex)+'/';
                }
                require(['../views/'+base+component+'.vue'],resolve);
            }
        }
        fmRoutes.push(fmRouter)
    })


    return fmRoutes;
}