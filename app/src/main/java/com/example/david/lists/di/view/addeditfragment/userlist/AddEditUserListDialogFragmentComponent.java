package com.example.david.lists.di.view.addeditfragment.userlist;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.di.view.addeditfragment.AddEditDialogFragmentCommonModule;
import com.example.david.lists.di.view.common.ViewCommonModule;
import com.example.david.lists.di.view.common.ViewScope;
import com.example.david.lists.view.addedit.userlist.AddEditUserListDialogFragment;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;

import static com.example.david.lists.di.view.addeditfragment.AddEditNamedConstants.ID;
import static com.example.david.lists.di.view.addeditfragment.AddEditNamedConstants.TITLE;

@ViewScope
@Component(modules = {
        AddEditUserListDialogFragmentModule.class,
        AddEditDialogFragmentCommonModule.class,
        ViewCommonModule.class
})
public interface AddEditUserListDialogFragmentComponent {
    void inject(AddEditUserListDialogFragment fragment);

    @Component.Builder
    interface Builder {
        AddEditUserListDialogFragmentComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder fragment(Fragment fragment);

        @BindsInstance
        Builder id(@Named(ID) String id);

        @BindsInstance
        Builder title(@Named(TITLE) String title);
    }
}
